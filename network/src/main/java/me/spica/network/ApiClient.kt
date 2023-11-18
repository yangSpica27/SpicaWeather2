package me.spica.network

import me.spica.persistence.entity.MzCity
import javax.inject.Inject


class ApiClient @Inject constructor(private val api: Api) {



    suspend fun getWeather(
        cities: List<MzCity>
    ) = api.getWeather(
        cityIds = cities.joinToString(",") {
            it.areaid.toString()
        }
    )


}