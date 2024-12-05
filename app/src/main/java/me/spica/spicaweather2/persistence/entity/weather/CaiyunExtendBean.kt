package me.spica.spicaweather2.persistence.entity.weather

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaiyunExtendBean(
  val alerts: List<AlertBean>,
  val description: String,
  val forecastKeypoint: String,
)
