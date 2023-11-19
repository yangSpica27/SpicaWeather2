package me.spica.spicaweather2.network.model.hefeng.hourly

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Hourly(
    @Json(name = "cloud")
    val cloud: String,
    @Json(name = "dew")
    val dew: String,
    @Json(name = "fxTime")
    val fxTime: String,
    @Json(name = "humidity")
    val humidity: String,
    @Json(name = "icon")
    val icon: String,
    @Json(name = "pop")
    val pop: String,
    @Json(name = "precip")
    val precip: String,
    @Json(name = "pressure")
    val pressure: String,
    @Json(name = "temp")
    val temp: String,
    @Json(name = "text")
    val text: String,
    @Json(name = "wind360")
    val wind360: String,
    @Json(name = "windDir")
    val windDir: String,
    @Json(name = "windScale")
    val windScale: String,
    @Json(name = "windSpeed")
    val windSpeed: String
)
