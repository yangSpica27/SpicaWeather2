package me.spica.spicaweather2.network

import com.skydoves.sandwich.ApiResponse
import me.spica.spicaweather2.network.model.BaseResponse
import me.spica.spicaweather2.network.model.batch.BatchWeatherList
import me.spica.spicaweather2.network.model.batch.BatchWeatherRequest
import me.spica.spicaweather2.network.model.batch.LocationRequest
import me.spica.spicaweather2.persistence.entity.city.CityBean
import javax.inject.Inject

@Suppress("unused")
class HeClient
@Inject
constructor(
  private val heService: HeService,
) {
  /**
   * 一次拉取多个城市的聚合天气。
   *
   * locationId 直接用 cityName：它是 CityBean 的主键，也是 Weather 表的关联键，
   * 服务端会原样回传，据此把结果对回城市。不要用经纬度字符串做匹配。
   */
  suspend fun getBatchWeather(cities: List<CityBean>): ApiResponse<BaseResponse<BatchWeatherList>> =
    heService.getBatchWeather(
      BatchWeatherRequest(
        locations =
          cities.map { city ->
            LocationRequest(
              locationId = city.cityName,
              longitude = city.lon,
              latitude = city.lat,
              name = city.cityName,
            )
          },
      ),
    )
}
