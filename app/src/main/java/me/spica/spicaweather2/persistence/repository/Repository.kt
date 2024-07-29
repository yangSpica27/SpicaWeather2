package me.spica.spicaweather2.persistence.repository

import me.spica.spicaweather2.persistence.entity.weather.Weather

interface Repository {
    fun fetchWeather(
        lon: String,
        lat: String,
        onError: (String?) -> Unit,
    ): kotlinx.coroutines.flow.Flow<Weather?>
}
