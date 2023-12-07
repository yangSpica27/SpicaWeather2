package me.spica.spicaweather2.network.model.caiyun

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "alert")
    val alert: Alert,
    @Json(name = "forecast_keypoint")
    val forecastKeypoint: String,
    @Json(name = "hourly")
    val hourly: Hourly
)
