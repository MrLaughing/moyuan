package com.mrlaughing.moyuan.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrlaughing.moyuan.data.local.db.entity.BookTrackingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookTrackingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<BookTrackingEntity>)

    @Query("SELECT * FROM book_tracking")
    fun observeAll(): Flow<List<BookTrackingEntity>>
}
