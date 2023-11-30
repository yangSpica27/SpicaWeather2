package me.spica.spicaweather2.ui.main

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.spica.spicaweather2.persistence.repository.CityRepository
import javax.inject.Inject

/**
 * 主页的viewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val cityRepository: CityRepository,
) : ViewModel() {





    val allCityWithWeather = cityRepository.allCitiesWithWeatherFlow()


    @WorkerThread
    fun  getAllCity() = cityRepository.allCityList()



}