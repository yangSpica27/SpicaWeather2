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


    @Query("SELECT * FROM t_city ORDER BY sort ASC")
    fun getCities(): Flow<List<CityBean>>

    @Query("SELECT * FROM t_city ORDER  by sort ASC")
    fun getAllList(): List<CityBean>


    @Transaction
    @Query("SELECT * FROM t_city ORDER BY sort ASC")
    fun getCitiesWithWeather(): Flow<List<CityWithWeather>>

    @ExperimentalCoroutinesApi
    fun getAllDistinctUntilChanged() = getCities().distinctUntilChanged()

    @ExperimentalCoroutinesApi
    fun getCitiesWithWeatherDistinctUntilChanged() = getCitiesWithWeather().distinctUntilChanged()

    // 交换顺序
    @Transaction
    fun exchangeSort(cityBean: CityBean, cityBean1: CityBean) {
        val temp = cityBean.sort
        cityBean.sort = cityBean1.sort
        cityBean1.sort = temp
        update(cityBean)
        update(cityBean1)
    }


    @Update
    fun update(cityBean: CityBean)

    @Delete
    fun deleteCity(cityBean: CityBean)

    @Query("DELETE FROM t_city WHERE cityName IN (:names)")
    fun deleteCitiesWithNames(names: List<String>)

}
