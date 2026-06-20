package com.mrlaughing.moyuan.data.model

enum class GrowthLevel(val displayName: String, val thresholdMinutes: Int) {
    LV1("嫩芽", 0),
    LV2("幼苗", 120),
    LV3("壮株", 480),
    LV4("繁盛", 1200),
    LV5("墨成", 2400);

    companion object {
        fun fromMinutes(minutes: Int): GrowthLevel {
            return entries.lastOrNull { minutes >= it.thresholdMinutes } ?: LV1
        }

        fun fromString(value: String): GrowthLevel {
            return entries.firstOrNull { it.name == value } ?: LV1
        }
    }
}
