package me.spica.spicaweather2.network.model.hefeng.daily

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Daily(
    @Json(name = "cloud")
    val cloud: String?,
    @Json(name = "fxDate")
    val fxDate: String,
    @Json(name = "humidity")
    val humidity: String,
    @Json(name = "iconDay")
    val iconDay: String,
    @Json(name = "iconNight")
    val iconNight: String,
    @Json(name = "moonPhase")
    val moonPhase: String,
    @Json(name = "moonPhaseIcon")
    val moonPhaseIcon: String,
    @Json(name = "moonrise")
    val moonrise: String,
    @Json(name = "moonset")
    val moonset: String,
    @Json(name = "precip")
    val precip: String,
    @Json(name = "pressure")
    val pressure: String,
    @Json(name = "sunrise")
    val sunrise: String,
    @Json(name = "sunset")
    val sunset: String,
    @Json(name = "tempMax")
    val tempMax: String,
    @Json(name = "tempMin")
    val tempMin: String,
    @Json(name = "textDay")
    val textDay: String,
    @Json(name = "textNight")
    val textNight: String,
    @Json(name = "uvIndex")
    val uvIndex: String,
    @Json(name = "vis")
    val vis: String,
    @Json(name = "wind360Day")
    val wind360Day: String,
    @Json(name = "wind360Night")
    val wind360Night: String,
    @Json(name = "windDirDay")
    val windDirDay: String,
    @Json(name = "windDirNight")
    val windDirNight: String,
    @Json(name = "windScaleDay")
    val windScaleDay: String,
    @Json(name = "windScaleNight")
    val windScaleNight: String,
    @Json(name = "windSpeedDay")
    val windSpeedDay: String,
    @Json(name = "windSpeedNight")
    val windSpeedNight: String,
)
