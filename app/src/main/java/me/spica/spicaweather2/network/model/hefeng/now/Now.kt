package me.spica.spicaweather2.network.model.hefeng.now

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Now(
    @Json(name = "cloud")
    val cloud: String,
    @Json(name = "dew")
    val dew: String,
    @Json(name = "feelsLike")
    val feelsLike: String,
    @Json(name = "humidity")
    val humidity: String,
    @Json(name = "icon")
    val icon: String,
    @Json(name = "obsTime")
    val obsTime: String,
    @Json(name = "precip")
    val precip: String,
    @Json(name = "pressure")
    val pressure: String,
    @Json(name = "temp")
    val temp: String,
    @Json(name = "text")
    val text: String,
    @Json(name = "vis")
    val vis: String,
    @Json(name = "wind360")
    val wind360: String,
    @Json(name = "windDir")
    val windDir: String,
    @Json(name = "windScale")
    val windScale: String,
    @Json(name = "windSpeed")
    val windSpeed: String
)
