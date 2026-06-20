package com.mrlaughing.moyuan.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenMetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: GardenMetaEntity)

    @Query("SELECT * FROM garden_meta WHERE id = 1")
    fun observe(): Flow<GardenMetaEntity?>

    @Query("SELECT * FROM garden_meta WHERE id = 1")
    suspend fun get(): GardenMetaEntity?
}
