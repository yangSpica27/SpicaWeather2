package me.spica.spicaweather2.persistence.entity.city

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Provinces(
    val provinces: List<Province>
)
