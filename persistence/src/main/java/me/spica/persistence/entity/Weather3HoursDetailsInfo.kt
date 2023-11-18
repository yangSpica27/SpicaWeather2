package me.spica.persistence.entity


import androidx.annotation.Keep


@Keep
data class Weather3HoursDetailsInfo(
    val endTime: String? = null,
    val highestTemperature: String? = null,
    val img: String? = null,
    val isRainFall: String? = null,
    val lowerestTemperature: String? = null,
    val precipitation: String? = null,
    val startTime: String? = null,
    val wd: String? = null,
    val weather: String? = null,
    val ws: String? = null
)