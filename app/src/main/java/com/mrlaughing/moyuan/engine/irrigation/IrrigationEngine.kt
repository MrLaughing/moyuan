package com.mrlaughing.moyuan.engine.irrigation

import com.mrlaughing.moyuan.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IrrigationEngine @Inject constructor() {

    fun calculateInkDrops(minutes: Int, multiplier: Float = 1.0f): Int =
        (minutes * Constants.INK_DROPS_PER_MINUTE * multiplier).toInt()

    fun calculateWithPathBonus(minutes: Int, pathMatched: Boolean, multiplier: Float = 1.0f): Int =
        (minutes * Constants.INK_DROPS_PER_MINUTE * (if (pathMatched) Constants.PATH_MATCH_MULTIPLIER else 1.0f) * multiplier).toInt()
}
