package me.spica.network

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.mappers.ApiSuccessModelMapper
import me.spica.persistence.entity.Weather

class SuccessMapper :ApiSuccessModelMapper<Response,List<Weather>> {

    override fun map(apiSuccessResponse: ApiResponse.Success<Response>): List<Weather> {
        if (apiSuccessResponse.data.code == 200) {
            return apiSuccessResponse.data.value
        }
        throw Exception(apiSuccessResponse.data.message)
    }
}