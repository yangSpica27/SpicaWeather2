package me.spica.spicaweather2.network.model

import com.skydoves.sandwich.ApiResponse
import me.spica.spicaweather2.network.model.caiyun.CaiyunBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import retrofit2.http.GET
import retrofit2.http.Query

@Suppress("unused")
interface HeService {

    // 聚合接口
    @GET("http://43.248.185.248:24040/api/weather/all")
    suspend fun getAllWeather(
        @Query("location")
        location: String,
    ): ApiResponse<BaseResponse<Weather>>

    @GET("https://weatherapi.market.xiaomi.com/wtr-v3/weather/all")
    suspend fun getMinutely(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,

    ): ApiResponse<CaiyunBean>
}
