package me.spica.spicaweather2.network.model.hefeng.air

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Now(
    @Json(name = "aqi")
    val aqi: String,
    @Json(name = "category")
    val category: String,
    @Json(name = "co")
    val co: String,
    @Json(name = "level")
    val level: String,
    @Json(name = "no2")
    val no2: String,
    @Json(name = "o3")
    val o3: String,
    @Json(name = "pm10")
    val pm10: String,
    @Json(name = "pm2p5")
    val pm2p5: String,
    @Json(name = "primary")
    val primary: String,
    @Json(name = "pubTime")
    val pubTime: String,
    @Json(name = "so2")
    val so2: String,
)
