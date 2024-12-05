package me.spica.spicaweather2.ui.add_city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.spica.spicaweather2.base.App
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.repository.CityRepository
import javax.inject.Inject

@HiltViewModel
class CityViewModel
@Inject
constructor(
  private val cityRepository: CityRepository,
) : ViewModel() {
  private val allCity = arrayListOf<CityBean>()

  private val citySearchKeyword = MutableSharedFlow<String>()

  val searchFlow =
    citySearchKeyword
      .map { keyword ->
        if (keyword.isEmpty()) return@map allCity
        return@map allCity.filter { it.cityName.contains(keyword) || it.sortName.contains(keyword) }
      }.flowOn(Dispatchers.IO)

  init {
    viewModelScope.launch(Dispatchers.IO) {
      allCity.addAll(CityBean.getAllCities(App.instance))
      citySearchKeyword.emit("")
    }
  }

  fun updateSearchKeyword(keyword: String) {
    viewModelScope.launch {
      citySearchKeyword.emit(keyword)
    }
  }

  fun getCount(): Int = cityRepository.getCount()

  suspend fun addCity(city: CityBean) {
    cityRepository.add(city)
  }
}
