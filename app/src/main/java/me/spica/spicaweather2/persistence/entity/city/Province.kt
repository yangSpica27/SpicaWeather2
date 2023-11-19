package me.spica.spicaweather2.persistence.entity.city

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Province(
    @Json(name = "children")
    val children: List<City>,
    @Json(name = "lat")
    val lat: String,
    @Json(name = "log")
    val log: String,
    @Json(name = "name")
    val name: String
)
