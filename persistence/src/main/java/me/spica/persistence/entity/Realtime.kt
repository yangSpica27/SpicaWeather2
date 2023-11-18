package me.spica.persistence.entity


import androidx.annotation.Keep


@Keep
data class Realtime(
    val img: String? = null,
    val sD: String? = null,
    val sendibleTemp: String? = null,
    val temp: String? = null,
    val time: String? = null,
    val wD: String? = null,
    val wS: String? = null,
    val weather: String? = null,
    val ziwaixian: String? = null
)