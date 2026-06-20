package com.mrlaughing.moyuan.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrlaughing.moyuan.data.local.db.entity.BaseSnapshotEntity

@Dao
interface BaseSnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BaseSnapshotEntity)

    @Query("SELECT * FROM base_snapshot WHERE id = 1")
    suspend fun get(): BaseSnapshotEntity?
}
