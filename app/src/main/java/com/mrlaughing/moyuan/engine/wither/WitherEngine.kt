package com.mrlaughing.moyuan.engine.wither

import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.model.GrowthLevel
import com.mrlaughing.moyuan.data.model.WitherStage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WitherEngine @Inject constructor() {

    data class WitherResult(val stage: WitherStage, val startDate: String?)

    fun calculate(lastReadDateIso: String, currentDateIso: String, currentStage: WitherStage): WitherResult {
        val last = if (lastReadDateIso.isBlank()) currentDateIso else lastReadDateIso
        val days = ChronoUnit.DAYS.between(
            LocalDate.parse(last),
            LocalDate.parse(currentDateIso)
        ).toInt().coerceAtLeast(0)
        val newStage = WitherStage.entries.sortedByDescending { it.thresholdDays }
            .firstOrNull { days >= it.thresholdDays } ?: WitherStage.NONE
        val start = if (newStage != WitherStage.NONE && currentStage == WitherStage.NONE) currentDateIso else null
        return WitherResult(newStage, start)
    }

    fun isRuined(plant: PlantStateEntity): Boolean {
        return WitherStage.fromString(plant.witherStage) == WitherStage.RUIN
    }

    fun rebuild(plant: PlantStateEntity): PlantStateEntity {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return plant.copy(
            accumulatedMinutes = 0,
            level = GrowthLevel.LV1.name,
            witherStage = WitherStage.NONE.name,
            witherStartDate = null,
            lastWateredDate = today,
            ruinsRebuildCount = plant.ruinsRebuildCount + 1,
            lastSyncedMinutes = 0
        )
    }
}
