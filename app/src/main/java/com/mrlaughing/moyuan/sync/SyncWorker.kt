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
            // 未配置 API Key 时直接返回成功，不重试
            if (!wereadRepository.isConfigured()) {
                return Result.success()
            }

            snapshotManager.initializeIfNeeded()

            val readData = wereadRepository.fetchReadData().getOrNull()
                ?: return Result.retry()
            val shelf = wereadRepository.fetchShelf().getOrNull()
                ?: return Result.retry()

            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            // 只映射一次 dailyRecord，复用
            val dailyRecord = wereadDataMapper.toDailyRecord(readData, today)
            statsRepository.upsertRecord(dailyRecord)
            statsRepository.upsertBooks(wereadDataMapper.toBookTracking(shelf.books))

            // 更新 BaseSnapshot
            snapshotManager.updateSnapshot(readData, shelf)

            val currentMeta = gardenRepository.getMeta()
                ?: GardenMetaEntity(id = 1, lastUpdatedDate = today)
            val plants = gardenRepository.getAllPlants()

            // 天气只 roll 一次，复用于计算和存储
            val weather = seasonEngine.rollWeather(currentMeta)
            val updatedPlants = gardenEngine.recalculate(dailyRecord, plants, currentMeta, weather)
            gardenRepository.upsertPlants(updatedPlants)

            gardenRepository.upsertMeta(gardenEngine.getGardenMetaForToday(currentMeta, weather))

            userPrefs.setLastSyncDate(today)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
