package me.spica.spicaweather2.persistence.entity.weather

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Suppress("unused")
class WeatherBeanConverter {
    private val moshi =
        Moshi
            .Builder()
            .add(DateAdapter)
            .build()

    private val nowWeatherAdapter = moshi.adapter(NowWeatherBean::class.java)

    private val airAdapter = moshi.adapter(AirBean::class.java)

    private val listOfAlertBeanType =
        Types.newParameterizedType(
            List::class.java,
            AlertBean::class.java,
        )

    private val alertAdapter = moshi.adapter<List<AlertBean>>(listOfAlertBeanType)

    private val listOfHourlyType =
        Types.newParameterizedType(
            List::class.java,
            HourlyWeatherBean::class.java,
        )

    private val hourlyWeatherAdapter =
        moshi.adapter<List<HourlyWeatherBean>>(listOfHourlyType)

    private val listOfDailyType =
        Types.newParameterizedType(
            List::class.java,
            DailyWeatherBean::class.java,
        )

    private val dailyWeatherAdapter =
        moshi.adapter<List<DailyWeatherBean>>(listOfDailyType)

    private val minuteliesType =
        Types.newParameterizedType(
            List::class.java,
            Minutely::class.java,
        )

    private val minutelyAdapter = moshi.adapter<List<Minutely>>(minuteliesType)

    private val listOfLifeIndexType =
        Types.newParameterizedType(
            List::class.java,
            LifeIndexBean::class.java,
        )

    private val lifeIndexWeatherAdapter =
        moshi.adapter<List<LifeIndexBean>>(listOfLifeIndexType)

    @TypeConverter
    fun nowWeatherBeanToString(nowWeatherBean: NowWeatherBean): String = nowWeatherAdapter.toJson(nowWeatherBean)

    @TypeConverter
    fun stringToNowWeatherBean(value: String): NowWeatherBean? = nowWeatherAdapter.fromJson(value)

    @TypeConverter
    fun hourlyWeatherToString(list: List<HourlyWeatherBean>): String = hourlyWeatherAdapter.toJson(list)

    @TypeConverter
    fun stringToHourly(string: String): List<HourlyWeatherBean> = hourlyWeatherAdapter.fromJson(string) ?: listOf()

    @TypeConverter
    fun minutiesToString(list: List<Minutely>): String = minutelyAdapter.toJson(list)

    @TypeConverter
    fun stringToMinuties(string: String): List<Minutely> = minutelyAdapter.fromJson(string) ?: listOf()

    @TypeConverter
    fun stringToDaily(json: String): List<DailyWeatherBean> = dailyWeatherAdapter.fromJson(json) ?: listOf()

    @TypeConverter
    fun dailyToString(list: List<DailyWeatherBean>): String = dailyWeatherAdapter.toJson(list)

    @TypeConverter
    fun lifeIndexToString(list: List<LifeIndexBean>): String = lifeIndexWeatherAdapter.toJson(list)

    @TypeConverter
    fun stringToLifeIndex(string: String): List<LifeIndexBean> = lifeIndexWeatherAdapter.fromJson(string) ?: listOf()

    @TypeConverter
    fun nowAirToAirBean(airBean: AirBean): String = airAdapter.toJson(airBean)

    @TypeConverter
    fun stringToAirBean(value: String): AirBean? = airAdapter.fromJson(value)

    @TypeConverter
    fun stringToAlertBean(value: String): List<AlertBean> = alertAdapter.fromJson(value) ?: listOf()

    @TypeConverter
    fun alertBeanToString(alertBeans: List<AlertBean>): String = alertAdapter.toJson(alertBeans)
}
