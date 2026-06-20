package com.mrlaughing.moyuan.data.repository

import com.mrlaughing.moyuan.data.local.db.dao.PlantStateDao
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepository @Inject constructor(
    private val plantStateDao: PlantStateDao
) {

    fun observeAll(): Flow<List<PlantStateEntity>> {
        return plantStateDao.observeAll()
    }

    suspend fun getAll(): List<PlantStateEntity> {
        return plantStateDao.getAll()
    }

    suspend fun upsert(entity: PlantStateEntity) {
        plantStateDao.upsert(entity)
    }
}
