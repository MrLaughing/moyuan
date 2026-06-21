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

            val readDataResult = wereadRepository.fetchReadData()
            if (readDataResult.isFailure) {
                Log.e(TAG, "SyncWorker: fetchReadData 失败", readDataResult.exceptionOrNull())
                return Result.failure()
            }
            val readData = readDataResult.getOrThrow()
            Log.d(TAG, "SyncWorker: readData 获取成功, todayMinutes=${readData.todayMinutes}, totalMinutes=${readData.totalMinutes}, dailyRecords.size=${readData.dailyRecords.size}")

            val shelfResult = wereadRepository.fetchShelf()
            if (shelfResult.isFailure) {
                Log.e(TAG, "SyncWorker: fetchShelf 失败", shelfResult.exceptionOrNull())
                return Result.failure()
            }
            val shelf = shelfResult.getOrThrow()
            Log.d(TAG, "SyncWorker: shelf 获取成功, totalBooks=${shelf.totalBooks}, books.size=${shelf.books.size}")

            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            // 保存所有历史日记录（含今天），使 SUM 查询能正确累加
            val allRecords = wereadDataMapper.toDailyRecordList(readData.dailyRecords)
            if (allRecords.isNotEmpty()) {
                statsRepository.upsertRecords(allRecords)
                Log.d(TAG, "SyncWorker: 保存了 ${allRecords.size} 条日记录")
            }
            // 兜底：如果 API 没返回 dailyRecords，至少保存今天的记录
            if (allRecords.none { it.date == today }) {
                val todayRecord = wereadDataMapper.toDailyRecord(readData, today)
                statsRepository.upsertRecord(todayRecord)
                Log.d(TAG, "SyncWorker: 兜底保存今天记录, minutes=${todayRecord.readMinutes}")
            }

            statsRepository.upsertBooks(wereadDataMapper.toBookTracking(shelf.books))
            Log.d(TAG, "SyncWorker: 保存了 ${shelf.books.size} 本书")

            snapshotManager.updateSnapshot(readData, shelf)

            val currentMeta = gardenRepository.getMeta()
                ?: GardenMetaEntity(id = 1, lastUpdatedDate = today)
            val plants = gardenRepository.getAllPlants()

            val weather = seasonEngine.rollWeather(currentMeta)
            val dailyRecord = allRecords.find { it.date == today }
                ?: wereadDataMapper.toDailyRecord(readData, today)
            val updatedPlants = gardenEngine.recalculate(dailyRecord, plants, currentMeta, weather)
            gardenRepository.upsertPlants(updatedPlants)

            // 把 API 返回的统计数据写入 meta
            val computedTotalMinutes =
                if (allRecords.isNotEmpty()) allRecords.sumOf { it.readMinutes }
                else readData.totalMinutes
            val newMeta = gardenEngine.getGardenMetaForToday(
                meta = currentMeta,
                weather = weather,
                totalMinutes = computedTotalMinutes,
                booksRead = readData.booksRead,
                streakDays = readData.streakDays,
                nightReadDays = readData.nightReadDays
            )
            gardenRepository.upsertMeta(newMeta)
            Log.d(TAG, "SyncWorker: meta 已更新, totalMinutes=$computedTotalMinutes, booksRead=${readData.booksRead}, streakDays=${readData.streakDays}")

            userPrefs.setLastSyncDate(today)
            Log.d(TAG, "SyncWorker: 同步完成")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "SyncWorker: 同步异常", e)
            Result.failure()
        }
    }
}
