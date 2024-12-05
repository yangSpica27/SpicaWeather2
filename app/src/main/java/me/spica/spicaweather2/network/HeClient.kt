package me.spica.spicaweather2.network

import com.skydoves.sandwich.ApiResponse
import me.spica.spicaweather2.network.model.BaseResponse
import me.spica.spicaweather2.network.model.caiyun.CaiyunBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import javax.inject.Inject

@Suppress("unused")
class HeClient
@Inject
constructor(
  private val heService: HeService,
) {
  // 聚合接口
  suspend fun getAllWeather(
    lon: String,
    lat: String,
  ): ApiResponse<BaseResponse<Weather>> =
    heService.getAllWeather(
      location = "$lon,$lat",
    )

  // 获取分钟级别天气和预警信息
  suspend fun getMinute(
    lon: String,
    lat: String,
  ): ApiResponse<CaiyunBean> = heService.getMinutely(lat.toDouble(), lon.toDouble())
}
