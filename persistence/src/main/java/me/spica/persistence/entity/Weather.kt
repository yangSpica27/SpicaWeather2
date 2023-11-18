package me.spica.persistence.entity


import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


// https://aider.meizu.com/app/weather/listWeather?cityIds=101190101
@Entity(tableName = "weather")
@Keep
data class Weather(
    @Ignore
    var alarms: List<Alarm> = listOf(),
    var city: String? = "",
    @PrimaryKey(autoGenerate = true)
    var cityid: Int? = 0,
    @Ignore
    var indexes: List<Indexe> = listOf(),
    @Ignore
    var pm25: Pm25? = Pm25(),
    var provinceName: String? = "",
    @Ignore
    var realtime: Realtime? = Realtime(),
    @Ignore
    var weatherDetailsInfo: WeatherDetailsInfo? = WeatherDetailsInfo(),
    @Ignore
    var weathers: List<WeatherX> = listOf()
)