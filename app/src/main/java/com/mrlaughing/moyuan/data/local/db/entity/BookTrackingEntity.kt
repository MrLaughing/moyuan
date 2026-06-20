package com.mrlaughing.moyuan.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_tracking")
data class BookTrackingEntity(
    @PrimaryKey val bookId: String,
    val title: String,
    val author: String,
    val cover: String,
    val readMinutes: Int = 0,
    val progress: Float = 0f,
    val finishStatus: Int = 0,
    val lastReadDate: String = "",
    val addedAt: String = ""
)
