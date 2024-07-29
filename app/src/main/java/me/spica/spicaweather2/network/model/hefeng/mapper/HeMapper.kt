package me.spica.spicaweather2.network.model.hefeng.mapper

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.mappers.ApiSuccessModelMapper
import me.spica.spicaweather2.network.model.BaseResponse
import me.spica.spicaweather2.network.model.HeCode
import me.spica.spicaweather2.network.model.hefeng.air.Air
import me.spica.spicaweather2.network.model.hefeng.daily.DailyWeather
import me.spica.spicaweather2.network.model.hefeng.hourly.HourlyWeather
import me.spica.spicaweather2.network.model.hefeng.index.LifeIndex
import me.spica.spicaweather2.network.model.hefeng.now.NowWeather
import me.spica.spicaweather2.persistence.entity.weather.AirBean
import me.spica.spicaweather2.persistence.entity.weather.DailyWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.HourlyWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.LifeIndexBean
import me.spica.spicaweather2.persistence.entity.weather.NowWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.persistence.entity.weather.toAir
import me.spica.spicaweather2.persistence.entity.weather.toDailyWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.toHourlyWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.toLifeIndexBean
import me.spica.spicaweather2.persistence.entity.weather.toNowWeatherBean

object SuccessDailyWeatherMapper : ApiSuccessModelMapper<DailyWeather, List<DailyWeatherBean>> {
    @Throws(RuntimeException::class)
    override fun map(apiSuccessResponse: ApiResponse.Success<DailyWeather>): List<DailyWeatherBean> {
        if (apiSuccessResponse.data.code == HeCode.Ok.code) {
            return apiSuccessResponse.data.daily.map {
                it.toDailyWeatherBean()
            }
        } else {
            throw RuntimeException(apiSuccessResponse.data.code)
        }
    }
}

object SuccessNowWeatherMapper : ApiSuccessModelMapper<NowWeather, NowWeatherBean> {
    @Throws(RuntimeException::class)
    override fun map(apiSuccessResponse: ApiResponse.Success<NowWeather>): NowWeatherBean {
        if (apiSuccessResponse.data.code == HeCode.Ok.code) {
            val now = apiSuccessResponse.data.now.toNowWeatherBean()
            now.fxLink = apiSuccessResponse.data.fxLink
            return now
        } else {
            throw RuntimeException(apiSuccessResponse.data.code)
        }
    }
}

object SuccessHourlyWeatherMapper : ApiSuccessModelMapper<HourlyWeather, List<HourlyWeatherBean>> {
    @Throws(RuntimeException::class)
    override fun map(apiSuccessResponse: ApiResponse.Success<HourlyWeather>): List<HourlyWeatherBean> {
        if (apiSuccessResponse.data.code == HeCode.Ok.code) {
            return apiSuccessResponse.data.hourly.map {
                it.toHourlyWeatherBean()
            }
        } else {
            throw RuntimeException(apiSuccessResponse.data.code)
        }
    }
}

object SuccessLifeIndexWeatherMapper : ApiSuccessModelMapper<LifeIndex, List<LifeIndexBean>> {
    @Throws(RuntimeException::class)
    override fun map(apiSuccessResponse: ApiResponse.Success<LifeIndex>): List<LifeIndexBean> {
        if (apiSuccessResponse.data.code == HeCode.Ok.code) {
            return apiSuccessResponse.data.daily.map {
                it.toLifeIndexBean()
            }
        } else {
            throw RuntimeException(apiSuccessResponse.data.code)
        }
    }
}

object SuccessAirMapper : ApiSuccessModelMapper<Air, AirBean> {
    @Throws(RuntimeException::class)
    override fun map(apiSuccessResponse: ApiResponse.Success<Air>): AirBean {
        if (apiSuccessResponse.data.code == HeCode.Ok.code) {
            val air = apiSuccessResponse.data.now.toAir()
            air.fxLink = apiSuccessResponse.data.fxLink
            return air
        } else {
            throw java.lang.RuntimeException(apiSuccessResponse.data.code)
        }
    }
}

// object SuccessMinutelyMapper : ApiSuccessModelMapper<CaiyunBean, CaiyunExtendBean> {
//
//    @Throws(RuntimeException::class)
//    override fun map(apiSuccessResponse: ApiResponse.Success<CaiyunBean>): CaiyunExtendBean {
//        if (apiSuccessResponse.data.status == "ok") {
//
//            return CaiyunExtendBean(
//                alerts = apiSuccessResponse.data.result.alert.content.map {
//                    AlertBean(title = it.title, text = it.description, sender = it.sender, startTime = it.startTime)
//                }.toList(),
//                description = apiSuccessResponse.data.result.hourly.description,
//                forecastKeypoint = apiSuccessResponse.data.result.forecastKeypoint
//            )
//        } else {
//            throw java.lang.RuntimeException(apiSuccessResponse.apiMessage)
//        }
//    }
// }

object SuccessWeatherMapper : ApiSuccessModelMapper<BaseResponse<Weather>, Weather> {
    override fun map(apiSuccessResponse: ApiResponse.Success<BaseResponse<Weather>>): Weather {
        if (apiSuccessResponse.data.data == null) throw java.lang.RuntimeException("暂无该地区数据")
        if (apiSuccessResponse.data.code != 200) throw java.lang.RuntimeException(apiSuccessResponse.data.message)
        return apiSuccessResponse.data.data!!
    }
}
