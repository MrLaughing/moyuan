package com.mrlaughing.moyuan.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_record")
data class DailyRecordEntity(
    @PrimaryKey val date: String,
    val readMinutes: Int = 0,
    val path: String = "",
    val isNightRead: Boolean = false,
    val booksReadOnDate: Int = 0,
    val syncedAt: String = ""
)
