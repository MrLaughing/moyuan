package com.mrlaughing.moyuan.data.model

enum class PlantPath(val displayName: String) {
    JIMO("积墨"),
    BINGZHU("秉烛"),
    SUIHAN("岁寒"),
    XUNFANG("寻芳"),
    HIDDEN("隐藏");

    companion object {
        fun fromString(value: String): PlantPath {
            return entries.firstOrNull { it.name == value } ?: HIDDEN
        }
    }
}
