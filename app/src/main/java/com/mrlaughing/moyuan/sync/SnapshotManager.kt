package com.mrlaughing.moyuan.sync

import com.mrlaughing.moyuan.data.local.db.dao.BaseSnapshotDao
import com.mrlaughing.moyuan.data.local.db.dao.GardenMetaDao
import com.mrlaughing.moyuan.data.local.db.entity.BaseSnapshotEntity
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.prefs.UserPrefs
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDto
import com.mrlaughing.moyuan.data.remote.dto.ShelfDto
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

    suspend fun updateSnapshot(dto: ReadDataDto, shelfDto: ShelfDto) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        baseSnapshotDao.upsert(
            BaseSnapshotEntity(
                id = 1,
                totalReadMinutes = dto.totalMinutes,
                booksRead = shelfDto.totalBooks,
                streakDays = dto.streakDays,
                nightReadDays = dto.nightReadDays,
                snapshotDate = today,
                createdAt = today
            )
        )
    }
}
