package me.spica.spicaweather2.ui.main

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

    // 获取所有城市的天气
    val allCityWithWeather = cityRepository.allCitiesWithWeatherFlow()

}
