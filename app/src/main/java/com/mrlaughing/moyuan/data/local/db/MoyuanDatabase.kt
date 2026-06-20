package com.mrlaughing.moyuan.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mrlaughing.moyuan.data.local.db.dao.BaseSnapshotDao
import com.mrlaughing.moyuan.data.local.db.dao.BookTrackingDao
import com.mrlaughing.moyuan.data.local.db.dao.DailyRecordDao
import com.mrlaughing.moyuan.data.local.db.dao.GardenMetaDao
import com.mrlaughing.moyuan.data.local.db.dao.PlantStateDao
import com.mrlaughing.moyuan.data.local.db.entity.BaseSnapshotEntity
import com.mrlaughing.moyuan.data.local.db.entity.BookTrackingEntity
import com.mrlaughing.moyuan.data.local.db.entity.DailyRecordEntity
import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity

@Database(
    entities = [
        BaseSnapshotEntity::class,
        DailyRecordEntity::class,
        PlantStateEntity::class,
        GardenMetaEntity::class,
        BookTrackingEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MoyuanDatabase : RoomDatabase() {

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE plant_state ADD COLUMN ruinsRebuildCount INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE plant_state ADD COLUMN lastSyncedMinutes INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }

    abstract fun baseSnapshotDao(): BaseSnapshotDao

    abstract fun dailyRecordDao(): DailyRecordDao

    abstract fun plantStateDao(): PlantStateDao

    abstract fun gardenMetaDao(): GardenMetaDao

    abstract fun bookTrackingDao(): BookTrackingDao
}
