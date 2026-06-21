package com.mrlaughing.moyuan.sync

import com.mrlaughing.moyuan.data.local.db.entity.BookTrackingEntity
import com.mrlaughing.moyuan.data.local.db.entity.DailyRecordEntity
import com.mrlaughing.moyuan.data.remote.dto.BookDto
import com.mrlaughing.moyuan.data.remote.dto.DailyRecordDto
import com.mrlaughing.moyuan.data.remote.dto.ReadDataDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WereadDataMapper @Inject constructor() {

    fun toDailyRecord(dto: ReadDataDto, date: String): DailyRecordEntity = DailyRecordEntity(
        date = date,
        readMinutes = dto.todayMinutes,
        path = "",
        isNightRead = false,
        booksReadOnDate = 0,
        syncedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )

    fun toDailyRecordList(dailyRecords: List<DailyRecordDto>): List<DailyRecordEntity> =
        dailyRecords.map { dto ->
            DailyRecordEntity(
                date = dto.date,
                readMinutes = dto.minutes,
                path = "",
                isNightRead = false,
                booksReadOnDate = dto.bookIds.size,
                syncedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }

    fun toBookTracking(bookDtos: List<BookDto>): List<BookTrackingEntity> = bookDtos.map { b ->
        BookTrackingEntity(
            bookId = b.bookId,
            title = b.title,
            author = b.author,
            cover = b.cover,
            readMinutes = b.recordReadingTime / 60,
            progress = b.progress,
            finishStatus = b.finishReading,
            lastReadDate = "",
            addedAt = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    }
}