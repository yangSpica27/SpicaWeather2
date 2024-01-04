package me.spica.spicaweather2.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import me.spica.spicaweather2.persistence.dao.CityDao
import me.spica.spicaweather2.persistence.dao.WeatherDao
import me.spica.spicaweather2.persistence.entity.CityWeatherRelation
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.entity.weather.Weather

@Database(
    entities =
    [CityBean::class,
        Weather::class,
        CityWeatherRelation::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

    abstract fun weatherDao(): WeatherDao
}
