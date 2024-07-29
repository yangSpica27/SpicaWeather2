package me.spica.spicaweather2.persistence.entity.weather

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.spica.spicaweather2.network.model.hefeng.now.Now
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

@JsonClass(generateAdapter = true)
@Parcelize
data class NowWeatherBean(
    val obsTime: String = "", // 更新时间
    val temp: Int, // 当前的温度
    val feelTemp: Int, // 体感温度
    val iconId: Int, // 图标
    val windSpeed: Int, // 风速
    val water: Int, // 湿度
    val windPa: Int, // 气压
    val weatherName: String,
    var fxLink: String? = "",
) : Parcelable {
    fun obsTime(): Date {
        try {
            return sdf.parse(obsTime) ?: Date()
        } catch (_: Exception) {
        }
        return Date()
    }

    // 获取体感温度的描述
    fun getFeelTempDescription(): String =
        if (feelTemp < 0) {
            "寒冷，注意保暖"
        } else if (feelTemp < 10) {
            "冷，注意保暖"
        } else if (feelTemp < 20) {
            "凉爽，适合户外活动"
        } else if (feelTemp < 26) {
            "舒适，适合户外活动"
        } else if (feelTemp < 35) {
            "炎热，注意防暑"
        } else {
            "酷热，注意防暑"
        }

    // 获取湿度的描述
    fun getWaterDescription(): String =
        if (water <= 30) {
            "干燥，注意补水"
        } else if (water <= 60) {
            "舒适"
        } else {
            "潮湿,体感温度会更高"
        }
}

fun Now.toNowWeatherBean(): NowWeatherBean {
    val updateDate = sdf.parse(this.obsTime) ?: Date()
    return NowWeatherBean(
        obsTime = this.obsTime,
        temp = this.temp.toIntOrNull() ?: 0,
        feelTemp = feelsLike.toIntOrNull() ?: 0,
        iconId = icon.toIntOrNull() ?: 0,
        windSpeed = windSpeed.toIntOrNull() ?: 0,
        water = humidity.toIntOrNull() ?: 0,
        windPa = pressure.toIntOrNull() ?: 0,
        weatherName = text,
    )
}
