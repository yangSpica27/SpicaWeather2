package me.spica.spicaweather2.persistence.entity

import androidx.room.Embedded
import androidx.room.Relation
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.entity.weather.Weather

data class CityWithWeather(
  @Embedded
  var city: CityBean,
  @Relation(
    parentColumn = "cityName",
    entityColumn = "cityName",
  )
  var weather: Weather? = null,
) {


  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CityWithWeather

    if (city != other.city) return false
    if (weather != other.weather) return false

    return true
  }

  override fun hashCode(): Int {
    var result = city.hashCode()
    result = 31 * result + (weather?.hashCode() ?: 0)
    return result
  }
}
