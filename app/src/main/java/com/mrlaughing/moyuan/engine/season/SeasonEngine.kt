package com.mrlaughing.moyuan.engine.season

import com.mrlaughing.moyuan.data.local.db.entity.GardenMetaEntity
import com.mrlaughing.moyuan.data.model.Season
import com.mrlaughing.moyuan.data.model.Weather
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SeasonEngine @Inject constructor() {

    fun getCurrentSeason(): Season {
        val month = LocalDate.now().monthValue
        return Season.fromMonth(month)
    }

    fun getSeason(month: Int): Season = Season.fromMonth(month)

    fun rollWeather(meta: GardenMetaEntity?): Weather {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        if (meta != null && meta.lastUpdatedDate == today) {
            return Weather.fromString(meta.currentWeather)
        }
        val rand = Random(System.currentTimeMillis())
        return when (rand.nextInt(100)) {
            in 0..2 -> Weather.FIRST_SNOW
            in 3..10 -> Weather.SPRING_RAIN
            in 11..20 -> Weather.MOONLIT
            else -> Weather.CLEAR
        }
    }

    fun getMultiplier(season: Season, weather: Weather): Float = season.multiplier * weather.multiplier
}
