package me.spica.spicaweather2.network

import com.skydoves.sandwich.ApiResponse
import me.spica.spicaweather2.network.model.hitokoto.HitokotoBean
import javax.inject.Inject

class HitokotoClient
    @Inject
    constructor(
        private val hitokotoService: HitokotoService,
    ) {
        suspend fun getHitokoto(): ApiResponse<HitokotoBean> = hitokotoService.getHitokoto()
    }
