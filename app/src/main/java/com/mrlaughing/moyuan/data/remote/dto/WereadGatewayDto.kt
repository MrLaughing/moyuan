package com.mrlaughing.moyuan.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 微信读书 Gateway API 请求格式
 */
data class GatewayRequest(
    @SerializedName("api_name") val apiName: String,
    @SerializedName("skill_version") val skillVersion: String = "1.0.3"
)

/**
 * 微信读书 Gateway API 响应格式
 */
data class GatewayResponse<T>(
    @SerializedName("errcode") val errcode: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: String?
)

/**
 * 阅读数据详情响应 (来自 /readdata/detail)
 */
data class ReadDataDetailResponse(
    @SerializedName("readTimes") val readTimes: Int = 0,
    @SerializedName("readDays") val readDays: Int = 0,
    @SerializedName("totalReadTime") val totalReadTime: Int = 0,
    @SerializedName("dayAverageReadTime") val dayAverageReadTime: Int = 0,
    @SerializedName("recordReadingTime") val recordReadingTime: List<DailyReadingRecord>? = null,
    @SerializedName("preferBooks") val preferBooks: List<PreferBook>? = null
)

data class DailyReadingRecord(
    @SerializedName("date") val date: String,
    @SerializedName("readTime") val readTime: Int = 0,
    @SerializedName("listenTime") val listenTime: Int = 0
)

data class PreferBook(
    @SerializedName("bookId") val bookId: String,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("readingTime") val readingTime: Int = 0,
    @SerializedName("progress") val progress: Int = 0
)

/**
 * 书架同步响应 (来自 /shelf/sync)
 */
data class ShelfSyncResponse(
    @SerializedName("books") val books: List<ShelfBook> = emptyList(),
    @SerializedName("totalBooks") val totalBooks: Int = 0
)

data class ShelfBook(
    @SerializedName("bookId") val bookId: String,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("readingProgress") val readingProgress: Float? = null,
    @SerializedName("finishReading") val finishReading: Int = 0,
    @SerializedName("category") val category: String? = null,
    @SerializedName("readingTime") val readingTime: Int = 0
)
