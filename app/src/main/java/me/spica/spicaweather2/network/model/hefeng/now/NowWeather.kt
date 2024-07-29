package me.spica.spicaweather2.network.model.hefeng.now

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NowWeather(
    @Json(name = "code")
    val code: String,
    @Json(name = "fxLink")
    val fxLink: String,
    @Json(name = "now")
    val now: Now,
    @Json(name = "refer")
    val refer: Refer,
    @Json(name = "updateTime")
    val updateTime: String,
)
