package com.mrlaughing.moyuan.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookDto(
    @SerializedName("bookId") val bookId: String,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("coverColor") val coverColor: String,
    @SerializedName("recordReadingTime") val recordReadingTime: Int = 0,
    @SerializedName("progress") val progress: Float = 0f,
    @SerializedName("finishReading") val finishReading: Int = 0,
    @SerializedName("category") val category: String = "",
    @SerializedName("noteCount") val noteCount: Int = 0
)
