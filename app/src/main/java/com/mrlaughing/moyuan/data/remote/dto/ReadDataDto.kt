package com.mrlaughing.moyuan.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReadDataDto(
    @SerializedName("totalMinutes") val totalMinutes: Int = 0,
    @SerializedName("todayMinutes") val todayMinutes: Int = 0,
    @SerializedName("thisWeekMinutes") val thisWeekMinutes: Int = 0,
    @SerializedName("thisMonthMinutes") val thisMonthMinutes: Int = 0,
    @SerializedName("thisYearMinutes") val thisYearMinutes: Int = 0,
    @SerializedName("booksRead") val booksRead: Int = 0,
    @SerializedName("streakDays") val streakDays: Int = 0,
    @SerializedName("maxStreakDays") val maxStreakDays: Int = 0,
    @SerializedName("nightReadDays") val nightReadDays: Int = 0,
    @SerializedName("lastReadDate") val lastReadDate: String = "",
    @SerializedName("date") val date: String = "",
    @SerializedName("readDays") val readDays: Int = 0,
    @SerializedName("finishBooksCount") val finishBooksCount: Int = 0,
    @SerializedName("noteCount") val noteCount: Int = 0,
    @SerializedName("highlightCount") val highlightCount: Int = 0,
    @SerializedName("preferCategory") val preferCategory: String = "",
    @SerializedName("preferAuthor") val preferAuthor: String = "",
    @SerializedName("depthLevel") val depthLevel: Int = 0,
    @SerializedName("depthDescription") val depthDescription: String = "",
    @SerializedName("books") val books: List<BookDto> = emptyList(),
    @SerializedName("dailyRecords") val dailyRecords: List<DailyRecordDto> = emptyList()
)

data class DailyRecordDto(
    @SerializedName("date") val date: String,
    @SerializedName("minutes") val minutes: Int = 0,
    @SerializedName("bookIds") val bookIds: List<String> = emptyList()
)
