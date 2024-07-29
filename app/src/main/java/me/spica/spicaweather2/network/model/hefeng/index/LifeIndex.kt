package me.spica.spicaweather2.network.model.hefeng.index

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LifeIndex(
    @Json(name = "code")
    val code: String,
    @Json(name = "daily")
    val daily: List<Daily>,
    @Json(name = "fxLink")
    val fxLink: String,
    @Json(name = "refer")
    val refer: Refer,
    @Json(name = "updateTime")
    val updateTime: String,
)
