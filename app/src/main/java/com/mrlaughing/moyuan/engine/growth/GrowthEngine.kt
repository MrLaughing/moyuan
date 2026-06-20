package com.mrlaughing.moyuan.engine.growth

import com.mrlaughing.moyuan.data.model.GrowthLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthEngine @Inject constructor() {

    fun calculateLevel(accumulatedMinutes: Int): GrowthLevel {
        return GrowthLevel.entries.sortedByDescending { it.thresholdMinutes }
            .firstOrNull { accumulatedMinutes >= it.thresholdMinutes } ?: GrowthLevel.LV1
    }

    fun progressToNextLevel(accumulatedMinutes: Int): Pair<Int, Int> {
        val current = calculateLevel(accumulatedMinutes)
        val entriesByThreshold = GrowthLevel.entries.sortedBy { it.thresholdMinutes }
        val currentIdx = entriesByThreshold.indexOf(current)
        val next = entriesByThreshold.getOrNull(currentIdx + 1)
        val progress = accumulatedMinutes - current.thresholdMinutes
        val remaining = if (next != null) next.thresholdMinutes - accumulatedMinutes else 0
        return progress to remaining
    }
}
