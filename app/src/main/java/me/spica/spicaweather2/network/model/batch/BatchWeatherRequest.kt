package me.spica.spicaweather2.network.model.batch

import com.squareup.moshi.JsonClass

/**
 * 批量天气查询请求
 *
 * 对应 POST http://106.54.25.152:4141/api/weather/batch
 */
@JsonClass(generateAdapter = true)
data class BatchWeatherRequest(
  val locations: List<LocationRequest> = emptyList(),
  val includeHourly: Boolean = true,
  val includeMinutely: Boolean = true,
  val includeAqi: Boolean = true,
  val includeAlerts: Boolean = true,
)

@JsonClass(generateAdapter = true)
data class LocationRequest(
  /**
   * 自定义位置ID，服务端原样回传，用于把结果关联回城市。
   * 本项目直接使用 [me.spica.spicaweather2.persistence.entity.city.CityBean.cityName]，
   * 因为它就是 Room 的主键，也是 Weather 表的关联键。
   */
  val locationId: String = "",
  val longitude: String = "",
  val latitude: String = "",
  val name: String? = null,
)
