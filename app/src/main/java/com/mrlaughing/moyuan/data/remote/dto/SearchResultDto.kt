package com.mrlaughing.moyuan.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchResultDto(
    @SerializedName("totalCount") val totalCount: Int = 0,
    @SerializedName("books") val books: List<SearchedBookDto> = emptyList()
)

data class SearchedBookDto(
    @SerializedName("bookId") val bookId: String,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("coverColor") val coverColor: String = "#FFFFFF",
    @SerializedName("rating") val rating: Float = 0f,
    @SerializedName("ratingCount") val ratingCount: Int = 0,
    @SerializedName("category") val category: String = "",
    @SerializedName("introduction") val introduction: String = "",
    @SerializedName("publishTime") val publishTime: String = "",
    @SerializedName("wordCount") val wordCount: Int = 0
)
