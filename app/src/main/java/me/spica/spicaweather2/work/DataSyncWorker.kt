package me.spica.spicaweather2.work

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.skydoves.sandwich.getOrNull
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
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
import me.spica.spicaweather2.persistence.entity.city.Province
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
        val provinces = arrayListOf<Province>()
        val cityList = arrayListOf<CityBean>()
        val moshi = Moshi.Builder().build()
        val listOfCardsType = Types.newParameterizedType(
            List::class.java, Province::class.java
        )

        val jsonAdapter = moshi.adapter<List<Province>>(listOfCardsType)

        provinces.addAll(
            jsonAdapter.fromJson(
                getJsonString(context)
            ) ?: listOf()
        )

        cityList.addAll(
            provinces.map {
                CityBean(
                    cityName = it.name,
                    sortName = PinyinHelper.convertToPinyinString
                        (it.name, "", PinyinFormat.WITHOUT_TONE),
                    lon = it.log,
                    lat = it.lat
                )
            }.filter {
                it.cityName.isNotEmpty()
            }
        )

        provinces.forEach {
            cityList.addAll(
                it.children.map { city ->
                    CityBean(
                        cityName = city.name,
                        sortName = PinyinHelper.convertToPinyinString
                            (city.name, "", PinyinFormat.WITHOUT_TONE),
                        lon = city.log,
                        lat = city.lat
                    )
                }
            )
        }

        cityList.sortBy {
            it.sortName
        }
        cityList[0].isSelected = true
        cityDao.insertCities(cityList.first())
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