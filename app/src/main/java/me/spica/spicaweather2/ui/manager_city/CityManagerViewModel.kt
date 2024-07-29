package me.spica.spicaweather2.ui.manager_city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.repository.CityRepository
import javax.inject.Inject

@HiltViewModel
class CityManagerViewModel
    @Inject
    constructor(
        private val cityRepository: CityRepository,
    ) : ViewModel() {
        // 通过Flow获取所有的城市
        val allCityWithWeather = cityRepository.allCitiesWithWeatherFlow()

        // 是否是选择模式
        private val _isSelectMode = MutableSharedFlow<Boolean>(1)

        val isSelectable: Flow<Boolean>
            get() = _isSelectMode

        val topTitle =
            _isSelectMode
                .map {
                    return@map if (it) {
                        "请选择城市"
                    } else {
                        "管理城市"
                    }
                }.distinctUntilChanged()

        fun setSelectable(isSelectable: Boolean) {
            _isSelectMode.tryEmit(isSelectable)
        }

        fun moveCity(
            from: CityBean,
            to: CityBean,
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                cityRepository.exchangeSort(from, to)
            }
        }

        // 删除选择的城市
        fun deleteCities(citiesNames: List<String>) {
            viewModelScope.launch(Dispatchers.IO) {
                cityRepository.deleteCitiesWithNames(citiesNames)
            }
        }
    }
