package me.spica.spicaweather2.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.spica.spicaweather2.persistence.dao.WeatherDao
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.entity.weather.CaiyunExtendBean
import me.spica.spicaweather2.persistence.repository.HeRepository
import javax.inject.Inject

@Suppress("unused")
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: HeRepository,
    private val weatherDao: WeatherDao
) : ViewModel() {

    private val cityFlow: MutableStateFlow<CityBean?> = MutableStateFlow(null)

    // 设置城市

    fun changeCity(cityBean: CityBean) {
        viewModelScope.launch {
            cityFlow.emit(
                CityBean(
                    cityName = cityBean.cityName,
                    sortName = cityBean.sortName,
                    lon = cityBean.lon,
                    lat = cityBean.lat,
                    isSelected = cityBean.isSelected
                )
            )
        }
    }

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    val errorMessage: Flow<String> = _errorMessage.filterNotNull()

    private val _isLoading = MutableStateFlow(false)

    private val alert: Flow<CaiyunExtendBean?> = cityFlow
        .filterNotNull()
        .flatMapLatest {
            return@flatMapLatest repository.fetchCaiyunExtend(
                lon = it.lon,
                lat = it.lat,
                onError = { message ->
                    _errorMessage.value = message
                }
            )
        }

    val weatherCacheFlow = cityFlow.filterNotNull().flatMapLatest {
        weatherDao.getWeatherFlowDistinctUntilChanged(it.cityName)
    }.flowOn(Dispatchers.IO)

    // 和风系 接口
    val weatherFlow = cityFlow
        .filterNotNull()
        .flatMapLatest {
            repository.fetchWeather(
                lon = it.lon,
                lat = it.lat,
                onError = { message ->
                    _errorMessage.value = message
                }
            )
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // 合并和风系接口和彩云系列接口的数据
            combine(weatherFlow, alert) { weather, alertBean ->
                kotlin.run {
                    // 和风天气的天气数据+彩云的天气预警贴士(彩云接口请求失败不影响显示)
                    weather?.descriptionForToday = alertBean?.forecastKeypoint ?: ""
                    weather?.descriptionForToWeek = alertBean?.description ?: ""
                    weather?.alerts = alertBean?.alerts ?: listOf()
                    weather?.cityName = cityFlow.value?.cityName ?: ""
                    weather
                }
            }.collectLatest {
                viewModelScope.launch(Dispatchers.IO) {
                    if (it != null) {
                        // 将最后的数据载入缓存
                        weatherDao.insertWeather(it)
                    }
                }
            }
        }
    }
}
