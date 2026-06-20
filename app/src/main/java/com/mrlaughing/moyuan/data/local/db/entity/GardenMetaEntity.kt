package com.mrlaughing.moyuan.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrlaughing.moyuan.data.model.Season
import com.mrlaughing.moyuan.data.model.Weather

@Entity(tableName = "garden_meta")
data class GardenMetaEntity(
    @PrimaryKey val id: Int = 1,
    val totalReadMinutes: Int = 0,
    val booksRead: Int = 0,
    val streakDays: Int = 0,
    val nightReadDays: Int = 0,
    val accumulatedMinutes: Int = 0,
    val currentSeason: String = Season.SPRING.name,
    val currentWeather: String = Weather.CLEAR.name,
    val lastUpdatedDate: String = "",
    val lastWateredDate: String? = null
)
