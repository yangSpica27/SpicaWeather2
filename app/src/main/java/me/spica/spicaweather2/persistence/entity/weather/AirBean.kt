package me.spica.spicaweather2.persistence.entity.weather

import com.squareup.moshi.JsonClass
import me.spica.spicaweather2.network.model.hefeng.air.Now

@JsonClass(generateAdapter = true)
data class AirBean(
    val aqi: Int, // 指数
    val category: String, // 名称
    val primary: String, // 主要污染物
    val pm10: Float, // PM10
    val pm2p5: Float, // PM25
    val no2: Float, // co2
    val so2: Float, // so2
    val co: Float, // co
    val o3: Float, // 臭氧
    var fxLink: String? = "",
)

fun Now.toAir(): AirBean =
    AirBean(
        aqi = this.aqi.toIntOrNull() ?: 0,
        category = this.category,
        primary = primary,
        pm10 = pm10.toFloatOrNull() ?: 0f,
        pm2p5 = pm2p5.toFloatOrNull() ?: 0f,
        no2 = no2.toFloatOrNull() ?: 0f,
        so2 = so2.toFloatOrNull() ?: 0f,
        co = co.toFloatOrNull() ?: 0f,
        o3 = o3.toFloatOrNull() ?: 0f,
    )
