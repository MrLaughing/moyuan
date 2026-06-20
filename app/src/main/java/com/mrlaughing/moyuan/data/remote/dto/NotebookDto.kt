package com.mrlaughing.moyuan.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotebookDto(
    @SerializedName("bookId") val bookId: String,
    @SerializedName("bookTitle") val bookTitle: String,
    @SerializedName("author") val author: String,
    @SerializedName("highlights") val highlights: List<HighlightDto> = emptyList(),
    @SerializedName("thoughts") val thoughts: List<ThoughtDto> = emptyList(),
    @SerializedName("reviewCount") val reviewCount: Int = 0,
    @SerializedName("lastReadDate") val lastReadDate: String = ""
)

data class HighlightDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("chapterTitle") val chapterTitle: String,
    @SerializedName("createdAt") val createdAt: String = "",
    @SerializedName("colorStyle") val colorStyle: Int = 0,
    @SerializedName("markType") val markType: Int = 1
)

data class ThoughtDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("chapterTitle") val chapterTitle: String,
    @SerializedName("createdAt") val createdAt: String = "",
    @SerializedName("abstract") val abstract: String = ""
)
