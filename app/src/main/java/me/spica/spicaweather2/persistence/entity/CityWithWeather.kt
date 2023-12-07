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
    var weather: Weather?
)
