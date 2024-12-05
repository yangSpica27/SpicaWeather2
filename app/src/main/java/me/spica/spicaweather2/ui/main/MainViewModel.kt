package me.spica.spicaweather2.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.spica.spicaweather2.persistence.repository.CityRepository
import javax.inject.Inject

/**
 * 主页的viewModel
 */
@HiltViewModel
class MainViewModel
@Inject
constructor(
  cityRepository: CityRepository,
) : ViewModel() {
  // 获取所有城市的天气
  val allCityWithWeather = cityRepository.allCitiesWithWeatherFlow()

  private val _currentPagerIndex = MutableStateFlow(0)

  val currentPagerIndex: Flow<Int>
    get() = _currentPagerIndex

  fun setCurrentPagerIndex(index: Int) {
    viewModelScope.launch {
      _currentPagerIndex.emit(index)
    }
  }
}
