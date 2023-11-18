package me.spica.network

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {


    @GET("https://aider.meizu.com/app/weather/listWeather")
    suspend fun getWeather(@Query("cityIds") cityIds: String = "101010100"): ApiResponse<Response>

}