package com.mrlaughing.moyuan.sync

import android.content.Context
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

    override suspend fun doWork(): Result {
        return try {
            if (!wereadRepository.isConfigured()) {
                return Result.success()
            }

            snapshotManager.initializeIfNeeded()

            val readData = wereadRepository.fetchReadData().getOrNull()
                ?: return Result.retry()
            val shelf = wereadRepository.fetchShelf().getOrNull()
                ?: return Result.retry()

            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            // 保存所有历史日记录（含今天），使 SUM 查询能正确累加
            val allRecords = wereadDataMapper.toDailyRecordList(readData.dailyRecords)
            if (allRecords.isNotEmpty()) {
                statsRepository.upsertRecords(allRecords)
            }
            // 兜底：如果 API 没返回 dailyRecords，至少保存今天的记录
            if (allRecords.none { it.date == today }) {
                val todayRecord = wereadDataMapper.toDailyRecord(readData, today)
                statsRepository.upsertRecord(todayRecord)
            }

            statsRepository.upsertBooks(wereadDataMapper.toBookTracking(shelf.books))

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
            gardenRepository.upsertMeta(
                gardenEngine.getGardenMetaForToday(
                    meta = currentMeta,
                    weather = weather,
                    totalMinutes = computedTotalMinutes,
                    booksRead = readData.booksRead,
                    streakDays = readData.streakDays,
                    nightReadDays = readData.nightReadDays
                )
            )

            userPrefs.setLastSyncDate(today)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}