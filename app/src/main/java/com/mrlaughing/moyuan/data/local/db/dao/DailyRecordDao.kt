package com.mrlaughing.moyuan.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrlaughing.moyuan.data.local.db.entity.DailyRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyRecordEntity)

    @Query("SELECT * FROM daily_record ORDER BY date DESC")
    fun observeAll(): Flow<List<DailyRecordEntity>>

    @Query("SELECT COALESCE(SUM(readMinutes),0) FROM daily_record")
    fun observeTotalMinutes(): Flow<Int>
}
