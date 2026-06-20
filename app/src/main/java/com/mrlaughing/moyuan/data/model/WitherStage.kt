package com.mrlaughing.moyuan.data.model

enum class WitherStage(val displayName: String, val thresholdDays: Int) {
    NONE("无", 0),
    FADE("初淡", 2),
    WITHER("渐枯", 4),
    SEVERE("将枯", 7),
    DEAD("枯寂", 14),
    RUIN("废墟", 30);

    companion object {
        fun fromDays(days: Int): WitherStage {
            return entries.sortedByDescending { it.thresholdDays }
                .firstOrNull { days >= it.thresholdDays } ?: NONE
        }

        fun fromString(value: String): WitherStage {
            return entries.firstOrNull { it.name == value } ?: NONE
        }
    }
}
