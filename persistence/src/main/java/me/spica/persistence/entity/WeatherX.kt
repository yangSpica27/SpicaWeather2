package me.spica.persistence.entity


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WeatherX(
    val date: String? = null,
    val img: String? = null,
    @SerializedName("sun_down_time")
    val sunDownTime: String? = null,
    @SerializedName("sun_rise_time")
    val sunRiseTime: String? = null,
    @SerializedName("temp_day_c")
    val tempDayC: String? = null,
    @SerializedName("temp_day_f")
    val tempDayF: String? = null,
    @SerializedName("temp_night_c")
    val tempNightC: String? = null,
    @SerializedName("temp_night_f")
    val tempNightF: String? = null,
    val wd: String? = null,
    val weather: String? = null,
    val week: String? = null,
    val ws: String? = null
)