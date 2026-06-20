package com.mrlaughing.moyuan.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrlaughing.moyuan.data.model.GrowthLevel
import com.mrlaughing.moyuan.data.model.WitherStage

@Entity(tableName = "plant_state")
data class PlantStateEntity(
    @PrimaryKey val plantId: String,
    val path: String,
    val accumulatedMinutes: Int = 0,
    val level: String = GrowthLevel.LV1.name,
    val witherStage: String = WitherStage.NONE.name,
    val witherStartDate: String? = null,
    val unlockedDate: String,
    val lastWateredDate: String? = null,
    val pathProgress: Int = 0,
    val ruinsRebuildCount: Int = 0,
    val lastSyncedMinutes: Int = 0
)
