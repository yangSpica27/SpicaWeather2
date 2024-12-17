package me.spica.spicaweather2.persistence.entity.weather

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.WeatherType
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
  val cloud: Int,
) : Parcelable {
  @IgnoredOnParcel
  val windSpeed: Int = dayWindSpeed.toIntOrNull() ?: 0

  fun getWeatherType(): WeatherType = WeatherCodeUtils.getWeatherCode(iconId)

  //

  // 获取紫外线强度的描述
  fun getUVLevelDescription(): String =
    when (uv.toIntOrNull()) {
      in 0..2 -> "低"
      in 3..5 -> "中等"
      in 6..7 -> "高"
      in 8..10 -> "很高"
      else -> "极高"
    }

  // 获取紫外线的描述
  fun getUVDescription(): String =
    when (uv.toIntOrNull()) {
      in 0..2 -> "不需采取防护措施"
      in 3..5 -> "涂擦 SPF 大于 15、PA+防晒护肤品"
      in 6..7 -> "尽量减少外出，需要涂抹高倍数防晒霜"
      in 8..10 -> "尽量减少外出，需要涂抹高倍数防晒霜"
      else -> "尽量减少外出，需要涂抹高倍数防晒霜"
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
      return sdf2.parse(sunriseDate)!!
    } catch (_: Exception) {
    }
    return sdf2.parse("6:00")!!
  }

  fun sunsetDate(): Date {
    try {
      return sdf2.parse(sunsetDate)!!
    } catch (_: Exception) {
    }
    return sdf2.parse("18:00")!!
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as DailyWeatherBean

    if (fxTime != other.fxTime) return false
    if (maxTemp != other.maxTemp) return false
    if (minTemp != other.minTemp) return false
    if (iconId != other.iconId) return false
    if (water != other.water) return false
    if (windPa != other.windPa) return false
    if (weatherNameDay != other.weatherNameDay) return false
    if (precip != other.precip) return false
    if (sunriseDate != other.sunriseDate) return false
    if (sunsetDate != other.sunsetDate) return false
    if (moonParse != other.moonParse) return false
    if (dayWindDir != other.dayWindDir) return false
    if (dayWindSpeed != other.dayWindSpeed) return false
    if (nightWindSpeed != other.nightWindSpeed) return false
    if (nightWindDir != other.nightWindDir) return false
    if (weatherNameNight != other.weatherNameNight) return false
    if (pressure != other.pressure) return false
    if (uv != other.uv) return false
    if (vis != other.vis) return false
    if (cloud != other.cloud) return false
    if (windSpeed != other.windSpeed) return false

    return true
  }

  override fun hashCode(): Int {
    var result = fxTime.hashCode()
    result = 31 * result + maxTemp
    result = 31 * result + minTemp
    result = 31 * result + iconId
    result = 31 * result + water
    result = 31 * result + windPa
    result = 31 * result + weatherNameDay.hashCode()
    result = 31 * result + precip.hashCode()
    result = 31 * result + sunriseDate.hashCode()
    result = 31 * result + sunsetDate.hashCode()
    result = 31 * result + moonParse.hashCode()
    result = 31 * result + dayWindDir.hashCode()
    result = 31 * result + dayWindSpeed.hashCode()
    result = 31 * result + nightWindSpeed.hashCode()
    result = 31 * result + nightWindDir.hashCode()
    result = 31 * result + weatherNameNight.hashCode()
    result = 31 * result + (pressure?.hashCode() ?: 0)
    result = 31 * result + uv.hashCode()
    result = 31 * result + vis
    result = 31 * result + cloud
    result = 31 * result + windSpeed
    return result
  }
}

