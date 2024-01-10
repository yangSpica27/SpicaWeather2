package me.spica.spicaweather2.ui.manager_city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.repository.CityRepository
import javax.inject.Inject

@HiltViewModel
class CityManagerViewModel @Inject constructor(
    private val cityRepository: CityRepository,
) : ViewModel() {

    // 通过Flow获取所有的城市
    val allCityWithWeather = cityRepository.allCitiesWithWeatherFlow()

    // 删除选择的城市
    fun deleteCity(cityBean: CityBean) {
        viewModelScope.launch(Dispatchers.IO) {
            cityRepository.deleteCity(cityBean)
        }
    }
}
