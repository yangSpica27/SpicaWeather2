package me.spica.spicaweather2.network

import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.spica.spicaweather2.network.model.hefeng.mapper.SuccessWeatherMapper
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.persistence.repository.Repository
import timber.log.Timber

/**
 * 和风天气源的Repository封装
 */
class HeRepository(
    private val heClient: HeClient,
) : Repository {
    // 获取天气数据
    override fun fetchWeather(
        lon: String,
        lat: String,
        onError: (String?) -> Unit,
    ): Flow<Weather?> =
        flow {
            val response = heClient.getAllWeather(lon, lat)
            response
                .suspendOnSuccess(SuccessWeatherMapper) {
                    Timber.e("请求成功")
                    emit(this)
                }.suspendOnFailure {
                    Timber.e("请求失败")
                    emit(null)
                    Timber.e(this.message())
                    onError(this.message())
                }.suspendOnError {
                    Timber.e("请求失败")
                    emit(null)
                    Timber.e(message())
                    onError(message())
                }
        }.flowOn(Dispatchers.IO)
}
