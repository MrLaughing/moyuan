package com.mrlaughing.moyuan.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_snapshot")
data class BaseSnapshotEntity(
    @PrimaryKey val id: Int = 1,
    val totalReadMinutes: Int = 0,
    val booksRead: Int = 0,
    val streakDays: Int = 0,
    val nightReadDays: Int = 0,
    val snapshotDate: String = "",
    val createdAt: String = ""
)
