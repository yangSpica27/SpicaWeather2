package me.spica.persistence.entity


import androidx.annotation.Keep



@Keep
data class Pm25(
    val advice: String? = null,
    val aqi: String? = null,
    val citycount: Int? = null,
    val cityrank: Int? = null,
    val co: String? = null,
    val color: String? = null,
    val level: String? = null,
    val no2: String? = null,
    val o3: String? = null,
    val pm10: String? = null,
    val pm25: String? = null,
    val quality: String? = null,
    val so2: String? = null,
    val timestamp: String? = null,
    val upDateTime: String? = null
)