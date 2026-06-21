package com.mrlaughing.moyuan.sync

import com.mrlaughing.moyuan.data.local.db.dao.BaseSnapshotDao
import com.mrlaughing.moyuan.data.local.db.dao.GardenMetaDao
import com.mrlaughing.moyuan.data.local.db.entity.BaseSnapshotEntity
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDetailResponse
import com.mrlaughing.moyuan.data.remote.dto.ShelfSyncResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnapshotManager @Inject constructor(
    private val baseSnapshotDao: BaseSnapshotDao,
    private val gardenMetaDao: GardenMetaDao,
    private val userPrefs: UserPrefs
) {

    suspend fun initializeIfNeeded() {
        val snapshot = baseSnapshotDao.get()
        if (snapshot == null) {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            baseSnapshotDao.upsert(BaseSnapshotEntity(id = 1, snapshotDate = today, createdAt = today))
            gardenMetaDao.upsert(GardenMetaEntity(id = 1, lastUpdatedDate = today))
            userPrefs.setInstallDate(today)
        }
    }

    suspend fun getSnapshot(): BaseSnapshotEntity? = baseSnapshotDao.get()

    /**
     * 更新基准快照
     * 注意：API 返回的是秒，需要转换为分钟
     */
    suspend fun updateSnapshot(readData: ReadDataDetailResponse, shelf: ShelfSyncResponse) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        baseSnapshotDao.upsert(
            BaseSnapshotEntity(
                id = 1,
                totalReadMinutes = readData.totalReadTime / 60, // 秒转分钟
                booksRead = shelf.totalBooks,
                streakDays = readData.readDays,
                nightReadDays = 0,
                snapshotDate = today,
                createdAt = today
            )
        )
    }
}
