package com.mrlaughing.moyuan.data.repository

import com.mrlaughing.moyuan.data.local.db.dao.DailyRecordDao
import com.mrlaughing.moyuan.data.local.db.dao.GardenMetaDao
import com.mrlaughing.moyuan.data.local.db.dao.PlantStateDao
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenRepository @Inject constructor(
    private val plantStateDao: PlantStateDao,
    private val gardenMetaDao: GardenMetaDao,
    private val dailyRecordDao: DailyRecordDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun observeAllPlants(): Flow<List<PlantStateEntity>> {
        return plantStateDao.observeAll()
    }

    fun observeMeta(): Flow<GardenMetaEntity?> {
        return gardenMetaDao.observe()
    }

    suspend fun upsertPlants(list: List<PlantStateEntity>) {
        withContext(ioDispatcher) {
            plantStateDao.upsertAll(list)
        }
    }

    suspend fun upsertMeta(meta: GardenMetaEntity) {
        withContext(ioDispatcher) {
            gardenMetaDao.upsert(meta)
        }
    }

    suspend fun getAllPlants(): List<PlantStateEntity> {
        return withContext(ioDispatcher) {
            plantStateDao.getAll()
        }
    }

    suspend fun getMeta(): GardenMetaEntity? {
        return withContext(ioDispatcher) {
            gardenMetaDao.get()
        }
    }

    fun observeTotalMinutes(): Flow<Int> {
        return dailyRecordDao.observeTotalMinutes()
    }
}
