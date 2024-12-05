package me.spica.spicaweather2.network.model.caiyun

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Hourly(
  @Json(name = "description")
  val description: String,
)
