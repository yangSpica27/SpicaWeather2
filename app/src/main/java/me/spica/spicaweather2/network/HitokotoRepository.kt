package me.spica.spicaweather2.network

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.flow.flow

class HitokotoRepository(private val hitokotoClient: HitokotoClient) {


    fun fetchHitokoto() = flow {
        val response = hitokotoClient.getHitokoto()
        response.suspendOnSuccess {
            emit(this.data)
        }.suspendOnError {
            emit(null)
        }.suspendOnFailure {
            emit(null)
        }
    }


}