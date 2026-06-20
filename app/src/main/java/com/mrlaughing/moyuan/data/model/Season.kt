package com.mrlaughing.moyuan.data.model

enum class Season(val displayName: String, val multiplier: Float) {
    SPRING("春·萌芽", 1.2f),
    SUMMER("夏·盛放", 1.2f),
    AUTUMN("秋·收获", 1.3f),
    WINTER("冬·沉淀", 1.2f);

    companion object {
        fun fromString(value: String): Season {
            return entries.firstOrNull { it.name == value } ?: SPRING
        }

        fun fromMonth(month: Int): Season {
            return when (month) {
                in 3..5 -> SPRING
                in 6..8 -> SUMMER
                in 9..11 -> AUTUMN
                else -> WINTER
            }
        }
    }
}
