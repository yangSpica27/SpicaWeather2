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
class CityRepository
    @Inject
    constructor(
        private val cityDao: CityDao,
    ) {
        /**
         * 获取所有城市的flow
         */
        fun allCityFlow() = cityDao.getAllDistinctUntilChanged().distinctUntilChanged().flowOn(Dispatchers.IO)

        /**
         * 获取所有城市的列表
         */
        fun allCityList() = cityDao.getAllList()

        fun allCitiesWithWeatherFlow() = cityDao.getCitiesWithWeatherDistinctUntilChanged().flowOn(Dispatchers.IO)

        @WorkerThread
        suspend fun add(cityBean: CityBean) =
            withContext(Dispatchers.IO) {
                cityDao.insertCities(cityBean)
            }

        @WorkerThread
        fun getCount(): Int = cityDao.getCount()

        /**
         * 删除城市
         */
        @WorkerThread
        fun deleteCity(cityBean: CityBean) {
            cityDao.deleteCity(cityBean = cityBean)
        }

        @WorkerThread
        fun exchangeSort(
            cityBean: CityBean,
            cityBean1: CityBean,
        ) {
            cityDao.exchangeSort(cityBean, cityBean1)
        }

        @WorkerThread
        fun deleteCitiesWithNames(citiesNames: List<String>) {
            cityDao.deleteCitiesWithNames(citiesNames)
        }
    }
