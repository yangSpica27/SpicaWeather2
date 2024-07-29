package me.spica.spicaweather2.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import me.spica.spicaweather2.persistence.entity.weather.Weather

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE cityName == (:cityName)")
    fun getWeather(cityName: String): Flow<Weather?>

    @Query("SELECT * FROM weather WHERE cityName == (:cityName)")
    fun getWeatherEntity(cityName: String): Weather?

    @ExperimentalCoroutinesApi
    fun getWeatherFlowDistinctUntilChanged(cityName: String) = getWeather(cityName).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: Weather)
}
