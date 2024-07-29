package me.spica.spicaweather2.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BaseResponse<T> constructor(
    val code: Int,
    var message: String,
    val data: T?,
)
