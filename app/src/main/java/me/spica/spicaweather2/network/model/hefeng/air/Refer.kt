package me.spica.spicaweather2.network.model.hefeng.air

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Refer(
    @Json(name = "license")
    val license: List<String>,
    @Json(name = "sources")
    val sources: List<String>,
)
