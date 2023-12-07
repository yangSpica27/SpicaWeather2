package me.spica.spicaweather2.persistence.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import me.spica.spicaweather2.persistence.dao.CityDao
import me.spica.spicaweather2.persistence.entity.city.CityBean
import javax.inject.Inject

@Suppress("unused")
class CityRepository @Inject constructor(
    private val cityDao: CityDao
) {

    /**
     * 获取所有城市的flow
     */
    fun allCityFlow() = cityDao.getAllDistinctUntilChanged().distinctUntilChanged().flowOn(Dispatchers.IO)

    /**
     * 获取所有城市的列表
     */
    fun allCityList() = cityDao.getAllList()

    fun allCitiesWithWeatherFlow() = cityDao.getCitiesWithWeather().flowOn(Dispatchers.IO)

    /**
     * 选择城市
     */
    @WorkerThread
    suspend fun selected(cityBean: CityBean) = withContext(Dispatchers.IO) {
        cityDao.getSelectedCity()?.let {
            it.isSelected = false
            cityDao.update(it)
        }
        cityBean.isSelected = true
        cityDao.insertCities(cityBean)
    }

    @WorkerThread
    suspend fun add(cityBean: CityBean) = withContext(Dispatchers.IO) {
        cityBean.isSelected = false
        cityDao.insertCities(cityBean)
    }

    /**
     * 删除城市
     */
    @WorkerThread
    fun deleteCity(cityBean: CityBean) {
        cityDao.deleteCity(cityBean = cityBean)
    }

    // 选择的城市
    fun selectedCityFlow() = cityDao.getSelectCity()
}
