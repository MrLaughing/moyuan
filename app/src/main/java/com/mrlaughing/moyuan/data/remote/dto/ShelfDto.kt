package com.mrlaughing.moyuan.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShelfDto(
    @SerializedName("totalBooks") val totalBooks: Int = 0,
    @SerializedName("books") val books: List<BookDto> = emptyList(),
    @SerializedName("syncDate") val syncDate: String = ""
)
