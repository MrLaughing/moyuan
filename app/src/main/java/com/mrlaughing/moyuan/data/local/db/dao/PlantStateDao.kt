package com.mrlaughing.moyuan.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PlantStateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<PlantStateEntity>)

    @Query("SELECT * FROM plant_state")
    fun observeAll(): Flow<List<PlantStateEntity>>

    @Query("SELECT * FROM plant_state")
    suspend fun getAll(): List<PlantStateEntity>
}
