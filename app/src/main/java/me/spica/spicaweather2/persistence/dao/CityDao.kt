@file:Suppress("unused")

package me.spica.spicaweather2.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.persistence.entity.city.CityBean

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(vararg cityBean: CityBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cityBean: List<CityBean>)

    @Query("SELECT * FROM t_city WHERE isSelected =:isSelect LIMIT 0,1")
    fun getSelectCity(isSelect: Boolean = true): Flow<CityBean?>

    @Query("SELECT * FROM t_city ORDER BY isSelected DESC , cityName DESC")
    fun getCities(): Flow<List<CityBean>>

    @Query("SELECT * FROM t_city ORDER  by cityName")
    fun getAllList(): List<CityBean>

    @Query("SELECT * FROM t_city WHERE isSelected == 1 LIMIT 1")
    fun getSelectedCity(): CityBean?

    @Transaction
    @Query("SELECT * FROM t_city")
    fun getCitiesWithWeather(): Flow<List<CityWithWeather>>

    @ExperimentalCoroutinesApi
    fun getAllDistinctUntilChanged() = getCities().distinctUntilChanged()

    @ExperimentalCoroutinesApi
    fun getCitiesWithWeatherDistinctUntilChanged() = getCitiesWithWeather().distinctUntilChanged()

    @Update
    fun update(cityBean: CityBean)

    @Delete
    fun deleteCity(cityBean: CityBean)
}
