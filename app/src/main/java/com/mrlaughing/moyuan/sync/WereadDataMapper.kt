package com.mrlaughing.moyuan.sync

import com.mrlaughing.moyuan.data.local.db.entity.BookTrackingEntity
import com.mrlaughing.moyuan.data.local.db.entity.DailyRecordEntity
import com.mrlaughing.moyuan.data.remote.dto.DailyReadingRecord
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDetailResponse
import com.mrlaughing.moyuan.data.remote.dto.ShelfBook
import com.mrlaughing.moyuan.data.remote.dto.ShelfSyncResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WereadDataMapper @Inject constructor() {

    /**
     * 将 API 每日阅读记录转为 DailyRecordEntity
     */
    fun toDailyRecord(dto: DailyReadingRecord): DailyRecordEntity = DailyRecordEntity(
        date = dto.date,
        readMinutes = dto.readTime / 60, // API 返回秒，转分钟
        path = "",
        isNightRead = false,
        booksReadOnDate = 0,
        syncedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )

    /**
     * 将 API 每日阅读记录列表转为 DailyRecordEntity 列表
     */
    fun toDailyRecordList(dailyRecords: List<DailyReadingRecord>): List<DailyRecordEntity> =
        dailyRecords.map { dto ->
            DailyRecordEntity(
                date = dto.date,
                readMinutes = dto.readTime / 60, // API 返回秒，转分钟
                path = "",
                isNightRead = false,
                booksReadOnDate = 0,
                syncedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }

    /**
     * 将书架书籍列表转为 BookTrackingEntity 列表
     */
    fun toBookTracking(books: List<ShelfBook>): List<BookTrackingEntity> = books.map { b ->
        BookTrackingEntity(
            bookId = b.bookId,
            title = b.title,
            author = b.author,
            cover = b.cover,
            readMinutes = b.readingTime / 60, // API 返回秒，转分钟
            progress = b.readingProgress ?: 0f,
            finishStatus = b.finishReading,
            lastReadDate = "",
            addedAt = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    }

    /**
     * 计算总阅读分钟数（秒转分钟）
     */
    fun totalSecondsToMinutes(totalSeconds: Int): Int = totalSeconds / 60

    /**
     * 构建今天的兜底记录
     */
    fun buildTodayFallbackRecord(readData: ReadDataDetailResponse, today: String): DailyRecordEntity =
        DailyRecordEntity(
            date = today,
            readMinutes = readData.totalReadTime / 60,
            path = "",
            isNightRead = false,
            booksReadOnDate = 0,
            syncedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
}
