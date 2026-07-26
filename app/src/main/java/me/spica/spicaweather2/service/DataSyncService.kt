package me.spica.spicaweather2.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.skydoves.sandwich.getOrThrow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.spicaweather2.network.HeClient
import me.spica.spicaweather2.network.model.BaseResponse
import me.spica.spicaweather2.network.model.batch.BatchWeatherList
import me.spica.spicaweather2.network.model.batch.toWeather
import me.spica.spicaweather2.persistence.dao.CityDao
import me.spica.spicaweather2.persistence.dao.WeatherDao
import me.spica.spicaweather2.persistence.entity.city.CityBean
import timber.log.Timber
import javax.inject.Inject

/**
 * 数据更新服务
 */
@AndroidEntryPoint
class DataSyncService : Service() {
  private val job: Job = SupervisorJob()

  private val scope = CoroutineScope(Dispatchers.IO + job)

  @Inject
  lateinit var heClient: HeClient

  @Inject
  lateinit var cityDao: CityDao

  @Inject
  lateinit var weatherDao: WeatherDao

  override fun onBind(intent: Intent): IBinder? = null

  override fun onCreate() {
    super.onCreate()



    scope.launch {
      // 获取到用户选择的城市列表
      val cityList = cityDao.getAllList().toMutableList()

      // 如果用户没有选择城市，则初始化城市列表
      if (cityList.isEmpty()) {
        initCityList(context = this@DataSyncService)
        cityList.addAll(cityDao.getAllList())
      }

      if (cityList.isNotEmpty()) {
        syncWeather(cityList)
      }

      // 通知主线程，服务执行完毕
      withContext(Dispatchers.Main) {
        // 停止服务
        stopSelf()
      }
    }
  }

  /**
   * 批量拉取并落库。
   */
  private suspend fun syncWeather(cityList: List<CityBean>) {
    val response: BaseResponse<BatchWeatherList>? =
      try {
        heClient.getBatchWeather(cityList).getOrThrow()
      } catch (e: Exception) {
        Timber.tag(TAG).e(e, "批量获取天气失败")
        null
      }

    if (response == null || response.code != 200 || response.data == null) {
      Timber.tag(TAG).e("批量获取天气返回异常 code=${response?.code} message=${response?.message}")
      return
    }

    val resultsByCity = response.data.results.associateBy { it.locationId }

    cityList.forEach { cityBean ->
      val result = resultsByCity[cityBean.cityName]
      if (result == null || !result.success) {
        Timber.tag(TAG).w("城市 ${cityBean.cityName} 无有效数据: ${result?.error}")
        return@forEach
      }

      val weather = result.data?.toWeather(cityBean.cityName)
      if (weather == null) {
        Timber.tag(TAG).w("城市 ${cityBean.cityName} 缺少实时天气，保留旧缓存")
        return@forEach
      }

      weather.welcomeText = "昭昭若日月之明，离离如星辰之行"
      weatherDao.insertWeather(weather)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    // 取消所有的任务
    job.cancel()
  }

  // 初始化城市列表
  private suspend fun initCityList(context: Context) {
    val cityList = CityBean.getAllCities(context)
    cityList.findLast { it.cityName == "南京" }?.let { cityDao.insertCities(it) }
  }

  companion object {
    private const val TAG = "DataSyncService"
  }
}
