package me.spica.spicaweather2.network.model.caiyun


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Suppress("unused")
@JsonClass(generateAdapter = true)
data class CaiyunBean(
    @Json(name = "api_status")
    val apiStatus: String,
    @Json(name = "result")
    val result: Result,
    @Json(name = "server_time")
    val serverTime: Int,
    @Json(name = "status")
    val status: String,
)