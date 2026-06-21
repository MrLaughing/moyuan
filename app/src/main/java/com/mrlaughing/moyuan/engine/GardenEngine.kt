package com.mrlaughing.moyuan.engine

import com.mrlaughing.moyuan.data.local.db.entity.DailyRecordEntity
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.model.Weather
import com.mrlaughing.moyuan.data.model.WitherStage
import com.mrlaughing.moyuan.engine.irrigation.IrrigationEngine
import com.mrlaughing.moyuan.engine.growth.GrowthEngine
import com.mrlaughing.moyuan.engine.season.SeasonEngine
import com.mrlaughing.moyuan.engine.unlock.UnlockEngine
import com.mrlaughing.moyuan.engine.wither.WitherEngine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenEngine @Inject constructor(
    private val irrigationEngine: IrrigationEngine,
    private val growthEngine: GrowthEngine,
    private val witherEngine: WitherEngine,
    private val seasonEngine: SeasonEngine,
    private val unlockEngine: UnlockEngine
) {

    suspend fun recalculate(
        dailyRecord: DailyRecordEntity,
        plantStates: List<PlantStateEntity>,
        gardenMeta: GardenMetaEntity,
        weather: Weather
    ): List<PlantStateEntity> {
        val season = seasonEngine.getCurrentSeason()
        val multiplier = seasonEngine.getMultiplier(season, weather)
        val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val hasReadToday = dailyRecord.readMinutes > 0

        val updated = plantStates.map { plant ->
            val currentStage = WitherStage.fromString(plant.witherStage)

            // 废墟植物不参与生长和枯萎计算
            if (witherEngine.isRuined(plant)) {
                return@map plant
            }

            // 枯萎计算：用 lastWateredDate（上次实际阅读日期）而非 dailyRecord.date
            val lastReadDate = plant.lastWateredDate ?: plant.unlockedDate
            val witherRes = witherEngine.calculate(lastReadDate, todayDate, currentStage)

            // 增量累加：只计入本次同步新增的分钟数，避免同日多次同步重复累加
            val minutesDelta = (dailyRecord.readMinutes - plant.lastSyncedMinutes).coerceAtLeast(0)
            val newProgress = plant.accumulatedMinutes + minutesDelta
            val newLevel = growthEngine.calculateLevel(newProgress)

            // 路径加成：只有植物路径与阅读记录路径精确匹配时才生效
            val pathMatched = plant.path == dailyRecord.path && dailyRecord.path.isNotBlank()
            val inkDrops = if (hasReadToday && minutesDelta > 0) {
                irrigationEngine.calculateWithPathBonus(minutesDelta, pathMatched, multiplier)
            } else 0

            // 有实际阅读时更新 lastWateredDate
            val newLastWateredDate = if (hasReadToday) todayDate else plant.lastWateredDate

            plant.copy(
                accumulatedMinutes = newProgress,
                level = if (witherRes.stage == WitherStage.NONE) newLevel.name else plant.level,
                witherStage = witherRes.stage.name,
                witherStartDate = if (witherRes.stage == WitherStage.NONE) null else witherRes.startDate ?: plant.witherStartDate,
                lastWateredDate = newLastWateredDate,
                pathProgress = plant.pathProgress + inkDrops,
                lastSyncedMinutes = dailyRecord.readMinutes
            )
        }
        return unlockEngine.checkAndUnlock(gardenMeta, updated).ifEmpty { unlockEngine.getStarterPlants(todayDate) }
    }

    fun getGardenMetaForToday(
        meta: GardenMetaEntity,
        weather: Weather,
        totalMinutes: Int = meta.totalReadMinutes,
        booksRead: Int = meta.booksRead,
        streakDays: Int = meta.streakDays,
        nightReadDays: Int = meta.nightReadDays
    ): GardenMetaEntity {
        val season = seasonEngine.getCurrentSeason()
        return meta.copy(
            currentSeason = season.name,
            currentWeather = weather.name,
            lastUpdatedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            totalReadMinutes = totalMinutes,
            accumulatedMinutes = totalMinutes,
            booksRead = booksRead,
            streakDays = streakDays,
            nightReadDays = nightReadDays
        )
    }
}
