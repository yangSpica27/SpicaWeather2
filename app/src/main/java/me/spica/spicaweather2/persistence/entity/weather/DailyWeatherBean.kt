package me.spica.spicaweather2.persistence.entity.weather

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.WeatherType
import me.spica.spicaweather2.network.model.hefeng.daily.Daily
import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
private val sdf2 = SimpleDateFormat("HH:mm", Locale.CHINA)

@Parcelize
@JsonClass(generateAdapter = true)
data class DailyWeatherBean(
    val fxTime: String = "", // 更新时间
    val maxTemp: Int, // 当日最高的温度
    val minTemp: Int, // 当日最低的温度
    val iconId: Int, // 图标
    val water: Int, // 湿度
    val windPa: Int, // 气压
    val weatherNameDay: String,
    val precip: Float, // 降水量
    val sunriseDate: String = "",
    val sunsetDate: String = "",
    val moonParse: String,
    val dayWindDir: String,
    val dayWindSpeed: String,
    val nightWindSpeed: String,
    val nightWindDir: String,
    val weatherNameNight: String,
    val pressure: String? = "",
    val uv: String,
    val vis: Int,
    val cloud: Int
) : Parcelable {

    @IgnoredOnParcel
    val windSpeed: Int = dayWindSpeed.toIntOrNull() ?: 0

    fun getWeatherType(): WeatherType {
        return WeatherCodeUtils.getWeatherCode(iconId)
    }

    fun fxTime(): Date {
        try {
            return sdf.parse(fxTime) ?: Date()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Date()
    }

    fun sunriseDate(): Date {
        try {
            return sdf2.parse(sunriseDate)
        } catch (_: Exception) {
        }
        return sdf2.parse("6:00")
    }

    fun sunsetDate(): Date {
        try {
            return sdf2.parse(sunsetDate)
        } catch (_: Exception) {
        }
        return sdf2.parse("18:00")
    }
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun Daily.toDailyWeatherBean(): DailyWeatherBean {
    return DailyWeatherBean(
        maxTemp = tempMax.toIntOrNull() ?: 0,
        minTemp = tempMin.toIntOrNull() ?: 0,
        iconId = iconDay.toIntOrNull() ?: 0,
        water = humidity.toIntOrNull() ?: 0,
        windPa = pressure.toIntOrNull() ?: 0,
        weatherNameDay = textDay,
        precip = precip.toFloatOrNull() ?: 0f,
        sunriseDate = this.sunrise,
        sunsetDate = this.sunset,
        moonParse = moonPhase,
        dayWindSpeed = windSpeedDay,
        dayWindDir = windDirDay,
        nightWindDir = windDirNight,
        nightWindSpeed = windSpeedNight,
        weatherNameNight = textNight,
        pressure = pressure,
        uv = uvIndex,
        vis = vis.toIntOrNull() ?: 0,
        cloud = cloud?.toIntOrNull() ?: 0
    )
}
