package me.spica.spicaweather2.network

import com.skydoves.sandwich.ApiResponse
import me.spica.spicaweather2.network.model.BaseResponse
import me.spica.spicaweather2.network.model.batch.BatchWeatherList
import me.spica.spicaweather2.network.model.batch.BatchWeatherRequest
import retrofit2.http.Body
import retrofit2.http.POST

@Suppress("unused")
interface HeService {
  /**
   * 批量聚合接口：一次请求拿到所有城市的实时/预报/分钟级降水/空气质量/预警。
   * 取代了旧的 :4040 /api/weather/all（单城 GET，schema 不兼容）。
   */
  @POST("http://106.54.25.152:4141/api/weather/batch")
  suspend fun getBatchWeather(
    @Body request: BatchWeatherRequest,
  ): ApiResponse<BaseResponse<BatchWeatherList>>
}
