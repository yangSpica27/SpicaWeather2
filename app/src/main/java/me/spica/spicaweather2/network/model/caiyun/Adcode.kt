package me.spica.spicaweather2.network.model.caiyun

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Adcode(
  @Json(name = "adcode")
  val adcode: Int,
  @Json(name = "name")
  val name: String,
)
