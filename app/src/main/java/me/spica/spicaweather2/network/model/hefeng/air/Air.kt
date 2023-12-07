package me.spica.spicaweather2.network.model.hefeng.air

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Air(
    @Json(name = "code")
    val code: String,
    @Json(name = "fxLink")
    val fxLink: String,
    @Json(name = "now")
    val now: Now,
    @Json(name = "refer")
    val refer: Refer,
    @Json(name = "station")
    val station: List<Station>?,
    @Json(name = "updateTime")
    val updateTime: String
)
