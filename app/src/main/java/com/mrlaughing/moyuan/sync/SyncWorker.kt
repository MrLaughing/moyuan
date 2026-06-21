package com.mrlaughing.moyuan.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.repository.GardenRepository
import com.mrlaughing.moyuan.data.repository.ReadStatsRepository
import com.mrlaughing.moyuan.data.repository.WereadRepository
import com.mrlaughing.moyuan.engine.GardenEngine
import com.mrlaughing.moyuan.engine.season.SeasonEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val wereadRepository: WereadRepository,
    private val statsRepository: ReadStatsRepository,
    private val gardenRepository: GardenRepository,
    private val gardenEngine: GardenEngine,
    private val wereadDataMapper: WereadDataMapper,
    private val snapshotManager: SnapshotManager,
    private val seasonEngine: SeasonEngine,
    private val userPrefs: UserPrefs
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "SyncWorker: 开始同步")

            if (!wereadRepository.isConfigured()) {
                Log.w(TAG, "SyncWorker: API Key 未配置，跳过同步")
                return Result.success()
            }

            snapshotManager.initializeIfNeeded()

            // 1. 获取阅读统计数据
            val readDataResult = wereadRepository.fetchReadData()
            if (readDataResult.isFailure) {
                Log.e(TAG, "SyncWorker: fetchReadData 失败", readDataResult.exceptionOrNull())
                return Result.failure()
            }
            val readData = readDataResult.getOrThrow()
            Log.d(TAG, "SyncWorker: readData 获取成功, totalReadTime=${readData.totalReadTime}s, readDays=${readData.readDays}, recordCount=${readData.recordReadingTime?.size ?: 0}")

            // 2. 获取书架数据
            val shelfResult = wereadRepository.fetchShelf()
            if (shelfResult.isFailure) {
                Log.e(TAG, "SyncWorker: fetchShelf 失败", shelfResult.exceptionOrNull())
                return Result.failure()
            }
            val shelf = shelfResult.getOrThrow()
            Log.d(TAG, "SyncWorker: shelf 获取成功, totalBooks=${shelf.totalBooks}, books.size=${shelf.books.size}")

            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            // 3. 保存所有历史日记录
            val dailyRecords = readData.recordReadingTime ?: emptyList()
            val allRecords = wereadDataMapper.toDailyRecordList(dailyRecords)
            if (allRecords.isNotEmpty()) {
                statsRepository.upsertRecords(allRecords)
                Log.d(TAG, "SyncWorker: 保存了 ${allRecords.size} 条日记录")
            }

            // 4. 兜底：如果没有日记录，至少保存今天的记录
            if (allRecords.none { it.date == today }) {
                val todayRecord = wereadDataMapper.buildTodayFallbackRecord(readData, today)
                statsRepository.upsertRecord(todayRecord)
                Log.d(TAG, "SyncWorker: 兜底保存今天记录, minutes=${todayRecord.readMinutes}")
            }

            // 5. 保存书籍数据
            statsRepository.upsertBooks(wereadDataMapper.toBookTracking(shelf.books))
            Log.d(TAG, "SyncWorker: 保存了 ${shelf.books.size} 本书")

            // 6. 更新快照
            snapshotManager.updateSnapshot(readData, shelf)

            // 7. 更新花园
            val currentMeta = gardenRepository.getMeta()
                ?: GardenMetaEntity(id = 1, lastUpdatedDate = today)
            val plants = gardenRepository.getAllPlants()

            val weather = seasonEngine.rollWeather(currentMeta)
            val dailyRecord = allRecords.find { it.date == today }
                ?: wereadDataMapper.buildTodayFallbackRecord(readData, today)
            val updatedPlants = gardenEngine.recalculate(dailyRecord, plants, currentMeta, weather)
            gardenRepository.upsertPlants(updatedPlants)

            // 8. 计算统计数据
            val totalMinutes = wereadDataMapper.totalSecondsToMinutes(readData.totalReadTime)
            val finishedBooks = shelf.books.count { it.finishReading == 1 }
            // 连续天数需要从日记录计算
            val streakDays = calculateStreakDays(allRecords)

            // 9. 更新 meta
            val newMeta = gardenEngine.getGardenMetaForToday(
                meta = currentMeta,
                weather = weather,
                totalMinutes = totalMinutes,
                booksRead = finishedBooks,
                streakDays = streakDays,
                nightReadDays = 0 // 新 API 不再直接返回夜读天数
            )
            gardenRepository.upsertMeta(newMeta)
            Log.d(TAG, "SyncWorker: meta 已更新, totalMinutes=$totalMinutes, finishedBooks=$finishedBooks, streakDays=$streakDays")

            userPrefs.setLastSyncDate(today)
            Log.d(TAG, "SyncWorker: 同步完成")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "SyncWorker: 同步异常", e)
            Result.failure()
        }
    }

    /**
     * 从日记录列表计算连续阅读天数
     */
    private fun calculateStreakDays(records: List<com.mrlaughing.moyuan.data.local.db.entity.DailyRecordEntity>): Int {
        if (records.isEmpty()) return 0

        // 按日期降序排序
        val sortedRecords = records.sortedByDescending { it.date }
        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val yesterdayStr = today.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

        // 检查最近两天是否有阅读
        val hasToday = sortedRecords.any { it.date == todayStr && it.readMinutes > 0 }
        val hasYesterday = sortedRecords.any { it.date == yesterdayStr && it.readMinutes > 0 }

        if (!hasToday && !hasYesterday) return 0

        var streak = 0
        var checkDate = if (hasToday) today else today.minusDays(1)

        while (true) {
            val dateStr = checkDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val hasRecord = sortedRecords.any { it.date == dateStr && it.readMinutes > 0 }
            if (hasRecord) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }
}
