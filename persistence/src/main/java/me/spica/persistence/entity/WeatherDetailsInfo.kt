package me.spica.persistence.entity


import androidx.annotation.Keep


@Keep
data class WeatherDetailsInfo(
    val publishTime: String? = null,
    val weather3HoursDetailsInfos: List<Weather3HoursDetailsInfo?>? = null
)