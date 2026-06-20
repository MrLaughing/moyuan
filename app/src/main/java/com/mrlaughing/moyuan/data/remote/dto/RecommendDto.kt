package com.mrlaughing.moyuan.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecommendDto(
    @SerializedName("totalCount") val totalCount: Int = 0,
    @SerializedName("books") val books: List<SearchedBookDto> = emptyList(),
    @SerializedName("reason") val reason: String = ""
)
