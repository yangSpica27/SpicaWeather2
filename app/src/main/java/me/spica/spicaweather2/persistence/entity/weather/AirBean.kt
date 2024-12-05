package me.spica.spicaweather2.persistence.entity.weather

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AirBean(
  val aqi: Int, // 指数
  val category: String, // 名称
  val primary: String, // 主要污染物
  val pm10: Float, // PM10
  val pm2p5: Float, // PM25
  val no2: Float, // co2
  val so2: Float, // so2
  val co: Float, // co
  val o3: Float, // 臭氧
  var fxLink: String? = "",
)

