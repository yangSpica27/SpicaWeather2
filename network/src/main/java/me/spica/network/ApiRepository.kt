package me.spica.network

import com.skydoves.sandwich.suspendMapFailure
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.flow.flow
import me.spica.persistence.entity.MzCity

class ApiRepository constructor(private val apiClient: ApiClient) {


    fun fetchWeather(
        cities: List<MzCity>
    ) = flow {

        apiClient.getWeather(cities)
            .suspendOnSuccess(SuccessMapper()) {
                emit(this)
            }.suspendOnError {
                emit(null)
            }.suspendMapFailure {
                emit(null)
            }

    }


}