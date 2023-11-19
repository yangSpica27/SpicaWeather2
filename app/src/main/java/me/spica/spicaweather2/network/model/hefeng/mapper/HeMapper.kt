package me.spica.spicaweather2.network.model.hefeng.mapper

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiSuccessModelMapper
import me.spica.spicaweather2.network.model.BaseResponse
import me.spica.spicaweather2.network.model.HeCode
import me.spica.spicaweather2.network.model.caiyun.CaiyunBean
import me.spica.spicaweather2.network.model.hefeng.air.Air
import me.spica.spicaweather2.network.model.hefeng.daily.DailyWeather
import me.spica.spicaweather2.network.model.hefeng.hourly.HourlyWeather
import me.spica.spicaweather2.network.model.hefeng.index.LifeIndex
import me.spica.spicaweather2.network.model.hefeng.now.NowWeather
import me.spica.spicaweather2.persistence.entity.weather.AirBean
import me.spica.spicaweather2.persistence.entity.weather.AlertBean
import me.spica.spicaweather2.persistence.entity.weather.CaiyunExtendBean
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
  override fun map(apiErrorResponse: ApiResponse.Success<DailyWeather>):
      List<DailyWeatherBean> {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.daily.map {
        it.toDailyWeatherBean()
      }
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}

object SuccessNowWeatherMapper : ApiSuccessModelMapper<NowWeather, NowWeatherBean> {
  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<NowWeather>):
      NowWeatherBean {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      val now = apiErrorResponse.data.now.toNowWeatherBean()
      now.fxLink = apiErrorResponse.data.fxLink
      return now
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}

object SuccessHourlyWeatherMapper : ApiSuccessModelMapper<HourlyWeather, List<HourlyWeatherBean>> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<HourlyWeather>):
      List<HourlyWeatherBean> {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.hourly.map {
        it.toHourlyWeatherBean()
      }
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}

object SuccessLifeIndexWeatherMapper : ApiSuccessModelMapper<LifeIndex, List<LifeIndexBean>> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<LifeIndex>):
      List<LifeIndexBean> {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.daily.map {
        it.toLifeIndexBean()
      }
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}


object SuccessAirMapper : ApiSuccessModelMapper<Air, AirBean> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<Air>): AirBean {
    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      val air = apiErrorResponse.data.now.toAir()
      air.fxLink = apiErrorResponse.data.fxLink
      return air
    } else {
      throw java.lang.RuntimeException(apiErrorResponse.data.code)
    }
  }

}

object SuccessMinutelyMapper : ApiSuccessModelMapper<CaiyunBean, CaiyunExtendBean> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<CaiyunBean>): CaiyunExtendBean {
    if (apiErrorResponse.data.status == "ok") {

      return CaiyunExtendBean(
        alerts = apiErrorResponse.data.result.alert.content.map {
          AlertBean(title = it.title, description = it.description, status = it.status, code = it.code, source = it.source)
        }.toList(),
        description = apiErrorResponse.data.result.hourly.description,
        forecastKeypoint = apiErrorResponse.data.result.forecastKeypoint
      )
    } else {
      throw java.lang.RuntimeException(apiErrorResponse.response.message())
    }
  }

}

object SuccessWeatherMapper : ApiSuccessModelMapper<BaseResponse<Weather>, Weather> {
  override fun map(apiErrorResponse: ApiResponse.Success<BaseResponse<Weather>>): Weather {
    if (apiErrorResponse.data.data == null) throw  java.lang.RuntimeException("暂无该地区数据")
    if (apiErrorResponse.data.code != 200) throw java.lang.RuntimeException(apiErrorResponse.data.message)
    return apiErrorResponse.data.data!!
  }

}