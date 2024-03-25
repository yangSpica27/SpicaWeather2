package me.spica.spicaweather2.network

import com.skydoves.sandwich.ApiResponse
import me.spica.spicaweather2.network.model.hitokoto.HitokotoBean
import retrofit2.http.GET

interface HitokotoService {

    @GET("https://v1.hitokoto.cn")
    suspend fun getHitokoto(): ApiResponse<HitokotoBean>
}