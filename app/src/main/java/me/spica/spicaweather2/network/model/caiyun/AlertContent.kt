package me.spica.spicaweather2.network.model.caiyun


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlertContent(
  @Json(name = "adcode")
  val adcode: String,
  @Json(name = "alertId")
  val alertId: String,
  @Json(name = "city")
  val city: String,
  @Json(name = "code")
  val code: String,
  @Json(name = "county")
  val county: String,
  @Json(name = "description")
  val description: String,
  @Json(name = "latlon")
  val latlon: List<Double>,
  @Json(name = "location")
  val location: String,
  @Json(name = "province")
  val province: String,
  @Json(name = "pubtimestamp")
  val pubtimestamp: Int,
  @Json(name = "regionId")
  val regionId: String,
  @Json(name = "request_status")
  val requestStatus: String,
  @Json(name = "source")
  val source: String,
  @Json(name = "status")
  val status: String,
  @Json(name = "title")
  val title: String
)