package me.spica.spicaweather2.persistence.repository

import me.spica.spicaweather2.persistence.entity.weather.CaiyunExtendBean
import me.spica.spicaweather2.persistence.entity.weather.Weather

interface Repository {



  // 获取彩云的信息用于拓展
  fun fetchCaiyunExtend(
    lon: String,
    lat: String,
    onError: (String?) -> Unit,
  ): kotlinx.coroutines.flow.Flow<CaiyunExtendBean?>


  fun fetchWeather(
    lon: String,
    lat: String,
    onError: (String?) -> Unit,
  ): kotlinx.coroutines.flow.Flow<Weather?>

}
