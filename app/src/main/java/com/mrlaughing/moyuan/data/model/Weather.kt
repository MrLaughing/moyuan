package com.mrlaughing.moyuan.data.model

enum class Weather(val displayName: String, val multiplier: Float) {
    CLEAR("晴", 1.0f),
    SPRING_RAIN("春雨", 1.3f),
    FIRST_SNOW("初雪", 1.8f),
    MOONLIT("月夜", 1.2f);

    companion object {
        fun fromString(value: String): Weather {
            return entries.firstOrNull { it.name == value } ?: CLEAR
        }
    }
}
