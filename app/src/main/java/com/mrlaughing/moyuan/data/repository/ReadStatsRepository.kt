package com.mrlaughing.moyuan.data.repository

import com.mrlaughing.moyuan.data.local.db.dao.BookTrackingDao
import com.mrlaughing.moyuan.data.local.db.dao.DailyRecordDao
import com.mrlaughing.moyuan.data.local.db.entity.BookTrackingEntity
import com.mrlaughing.moyuan.data.local.db.entity.DailyRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadStatsRepository @Inject constructor(
    private val dailyRecordDao: DailyRecordDao,
    private val bookTrackingDao: BookTrackingDao
) {

    fun observeDailyRecords(): Flow<List<DailyRecordEntity>> {
        return dailyRecordDao.observeAll()
    }

    fun observeBooks(): Flow<List<BookTrackingEntity>> {
        return bookTrackingDao.observeAll()
    }

    suspend fun upsertRecord(record: DailyRecordEntity) {
        dailyRecordDao.upsert(record)
    }

    suspend fun upsertRecords(records: List<DailyRecordEntity>) {
        dailyRecordDao.upsertAll(records)
    }

    suspend fun upsertBooks(books: List<BookTrackingEntity>) {
        bookTrackingDao.upsertAll(books)
    }

    fun observeTotalMinutes(): Flow<Int> {
        return dailyRecordDao.observeTotalMinutes()
    }
}