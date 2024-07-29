package me.spica.spicaweather2.network.model.hefeng.index

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Daily(
    @Json(name = "category")
    val category: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "level")
    val level: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "text")
    val text: String?,
    @Json(name = "type")
    val type: String,
)
