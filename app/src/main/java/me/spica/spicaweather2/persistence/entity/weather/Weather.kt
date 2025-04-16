package me.spica.spicaweather2.persistence.entity.weather

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherType
import me.spica.spicaweather2.common.getIconRes
import timber.log.Timber

@Entity
@TypeConverters(WeatherBeanConverter::class)
@JsonClass(generateAdapter = true)
data class Weather(
  val todayWeather: NowWeatherBean,
  val dailyWeather: List<DailyWeatherBean>,
  val hourlyWeather: List<HourlyWeatherBean>,
  val lifeIndexes: List<LifeIndexBean>,
  val air: AirBean,
  @PrimaryKey(autoGenerate = false)
  var cityName: String = "",
  var descriptionForToday: String? = "", // 今天的气象描述
  var descriptionForToWeek: String? = "", // 一周的气象描述
  @Json(name = "warnings")
  var alerts: List<AlertBean> = arrayListOf(),
  @Json(name = "minutely")
  var minutelies: List<Minutely> = arrayListOf(),
  var welcomeText: String = "",
) {
  fun getWeatherType(): WeatherType {
    when (todayWeather.iconId) {
      302, 304 -> {
        return WeatherType.WEATHER_THUNDERSTORM
      }

      100, 150 -> {
        return WeatherType.WEATHER_SUNNY
      }

      101, 151, 153, 103 -> {
        return WeatherType.WEATHER_CLOUDY
      }

      102, 104, 152, 154 -> {
        return WeatherType.WEATHER_FORECAST
      }

      300, 301, 303, 305, 306,
      307, 308, 309, 310, 311, 312,
      314, 315, 316, 317, 318, 350, 351,
      399,
        -> {
        return WeatherType.WEATHER_RAINY
      }

      400, 401, 402, 403, 408, 409, 410,
        -> {
        return WeatherType.WEATHER_SNOW
      }

      404, 405, 406, 456, 457, 499 -> {
        return WeatherType.WEATHER_SLEET
      }

      500, 501, 502 -> {
        return WeatherType.WEATHER_FOG
      }

      503, 504, 505, 506, 507, 508 -> {
        return WeatherType.WEATHER_SANDSTORM
      }

      509, 510,
      511, 512, 513, 514, 515,
        -> {
        return WeatherType.WEATHER_HAZE
      }

      313 -> {
        return WeatherType.WEATHER_HAIL
      }

      0 -> {
        return WeatherType.WEATHER_THUNDER
      }
    }
    Timber.tag("Weather").e("未知天气类型${todayWeather.iconId}")
    return WeatherType.WEATHER_SUNNY
  }

  @DrawableRes
  fun getWeatherIcon(): Int = getWeatherType().getIconRes()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Weather

    if (todayWeather != other.todayWeather) return false
    if (dailyWeather != other.dailyWeather) return false
    if (hourlyWeather != other.hourlyWeather) return false
    if (lifeIndexes != other.lifeIndexes) return false
    if (air != other.air) return false
    if (cityName != other.cityName) return false
    if (descriptionForToday != other.descriptionForToday) return false
    if (descriptionForToWeek != other.descriptionForToWeek) return false
    if (alerts != other.alerts) return false
    if (minutelies != other.minutelies) return false
    if (welcomeText != other.welcomeText) return false

    return true
  }

  override fun hashCode(): Int {
    var result = todayWeather.hashCode()
    result = 31 * result + dailyWeather.hashCode()
    result = 31 * result + hourlyWeather.hashCode()
    result = 31 * result + lifeIndexes.hashCode()
    result = 31 * result + air.hashCode()
    result = 31 * result + cityName.hashCode()
    result = 31 * result + (descriptionForToday?.hashCode() ?: 0)
    result = 31 * result + (descriptionForToWeek?.hashCode() ?: 0)
    result = 31 * result + alerts.hashCode()
    result = 31 * result + minutelies.hashCode()
    result = 31 * result + welcomeText.hashCode()
    return result
  }

}
