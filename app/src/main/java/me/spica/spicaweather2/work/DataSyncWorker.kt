package me.spica.spicaweather2.work

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.skydoves.sandwich.getOrNull
import com.skydoves.sandwich.getOrThrow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.spicaweather2.network.HeClient
import me.spica.spicaweather2.network.HitokotoClient
import me.spica.spicaweather2.network.model.BaseResponse
import me.spica.spicaweather2.persistence.dao.CityDao
import me.spica.spicaweather2.persistence.dao.WeatherDao
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import javax.inject.Inject

/**
 * 数据更新服务
 */
@AndroidEntryPoint
class DataSyncWorker : Service() {
    private val job: Job = SupervisorJob()

    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var heClient: HeClient

    @Inject
    lateinit var hitokotoClient: HitokotoClient

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
                initCityList(context = this@DataSyncWorker)
                cityList.addAll(cityDao.getAllList())
            }

            val ds: ArrayList<Deferred<Boolean>> = arrayListOf()

            cityList
                .forEach { cityBean ->
                    val res: Deferred<Boolean> =
                        async(Dispatchers.IO, CoroutineStart.DEFAULT) {
                            // 获取天气数据
                            val weatherResponse: BaseResponse<Weather>? =
                                try {
                                    heClient.getAllWeather(cityBean.lon, cityBean.lat).getOrThrow()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }

                            // 获取一言数据
                            val hitokotoResponse = hitokotoClient.getHitokoto().getOrNull()

                            // 如果获取到了数据，则插入到数据库中
                            weatherResponse?.data?.let { weather ->
                                weather.cityName = cityBean.cityName
                                weather.welcomeText =
                                    hitokotoResponse?.hitokoto ?: "昭昭若日月之明，离离如星辰之行"
                                weatherDao.insertWeather(weather)
                                return@async true
                            }
                            // 如果没有获取到数据，则返回 false
                            return@async false
                        }
                    ds.add(res)
                }

            // 等待所有的异步任务执行完毕
            ds.forEach { deferred ->
                deferred.await()
            }

            // 通知主线程，服务执行完毕
            withContext(Dispatchers.Main) {
                // 停止服务
                stopSelf()
            }
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
}
