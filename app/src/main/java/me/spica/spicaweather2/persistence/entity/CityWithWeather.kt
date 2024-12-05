package me.spica.spicaweather2.persistence.entity

import androidx.room.Embedded
import androidx.room.Relation
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.list.ListDiffer
import me.spica.spicaweather2.view.list.SimpleDiffCallback

data class CityWithWeather(
  @Embedded
  var city: CityBean,
  @Relation(
    parentColumn = "cityName",
    entityColumn = "cityName",
  )
  var weather: Weather?,
) {
  companion object {
    val diffFactory =
      ListDiffer.Blocking(
        object : SimpleDiffCallback<CityWithWeather>() {
          override fun areContentsTheSame(
            oldItem: CityWithWeather,
            newItem: CityWithWeather,
          ) = oldItem == newItem
        },
      )
  }

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
