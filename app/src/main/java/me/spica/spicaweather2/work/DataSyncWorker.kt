package me.spica.spicaweather2.work

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.skydoves.sandwich.getOrNull
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
import me.spica.spicaweather2.network.model.HeClient
import me.spica.spicaweather2.persistence.dao.CityDao
import me.spica.spicaweather2.persistence.dao.WeatherDao
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.persistence.entity.weather.AlertBean
import me.spica.spicaweather2.persistence.entity.weather.CaiyunExtendBean
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

@AndroidEntryPoint
class DataSyncWorker : Service() {
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
            val cityList = cityDao.getAllList()

            if (cityList.isEmpty()) {
                initCityList(context = this@DataSyncWorker)
                return@launch
            }

            val ds: ArrayList<Deferred<Boolean>> = arrayListOf()
            cityDao.getAllList()
                .forEach { cityBean ->
                val res: Deferred<Boolean> = async(Dispatchers.IO, CoroutineStart.DEFAULT) {
                    val weatherResponse = heClient.getAllWeather(cityBean.lon, cityBean.lat).getOrNull()
                    val minuteBaseResponse = heClient.getMinute(cityBean.lon, cityBean.lat).getOrNull()

                    var caiyunExtBean: CaiyunExtendBean? = null

                    if (minuteBaseResponse != null) {
                        caiyunExtBean = CaiyunExtendBean(
                            alerts = minuteBaseResponse.result.alert.content.map {
                                AlertBean(title = it.title, description = it.description, status = it.status, code = it.code, source = it.source)
                            },
                            description = minuteBaseResponse.result.hourly.description,
                            forecastKeypoint = minuteBaseResponse.result.forecastKeypoint
                        )
                    }

                    weatherResponse?.data?.let { weather ->
                        weather.descriptionForToday = caiyunExtBean?.forecastKeypoint ?: ""
                        weather.descriptionForToWeek = caiyunExtBean?.description ?: ""
                        weather.alerts = caiyunExtBean?.alerts ?: listOf()
                        weather.cityName = cityBean.cityName
                        weatherDao.insertWeather(weather)
                        return@async true
                    }

                    return@async false
                }
                ds.add(res)
            }

            ds.forEach { deferred ->
                deferred.await()
            }

            withContext(Dispatchers.Main) {
                stopSelf()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun initCityList(context: Context) {
        val  cityList = CityBean.getAllCities(context)
        cityDao.insertCities(cityList.first())
        cityDao.insertCities(cityList[2])
    }


    @Throws(IOException::class)
    private fun getJsonString(context: Context): String {
        var br: BufferedReader? = null
        val sb = StringBuilder()
        try {
            val manager = context.assets
            br = BufferedReader(InputStreamReader(manager.open("city.json")))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            try {
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

}