package me.spica.spicaweather2.persistence.entity

import androidx.room.Entity

@Entity(
    tableName = "t_city_weather",
    primaryKeys = ["cityName", "weatherCityName"],
)
data class CityWeatherRelation(
    var cityName: String,
    var weatherCityName: String
)
