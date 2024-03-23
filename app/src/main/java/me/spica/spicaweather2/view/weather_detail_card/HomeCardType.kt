package me.spica.spicaweather2.view.weather_detail_card

import android.content.Context
import android.view.View
import me.spica.spicaweather2.view.NowWeatherInfoCard
import me.spica.spicaweather2.view.view_group.AirCardLayout
import me.spica.spicaweather2.view.view_group.DailyWeatherLayout
import me.spica.spicaweather2.view.view_group.HourlyCardLayout
import me.spica.spicaweather2.view.view_group.TodayDescLayout
import me.spica.spicaweather2.view.view_group.SunriseCardLayout
import me.spica.spicaweather2.view.view_group.TipsLayout

enum class HomeCardType(val code: Int) {
    TODAY_DESC(-1),
    NOW_WEATHER(0), // 现在的天气
//    TODAY_EXTRA(1),// 生活指数
    HOUR_WEATHER(2), // 小时天气
    DAY_WEATHER(3), // 日级天气
    SUNRISE(4), // 日出日落
    AIR(5),
    TIPS(6); // 生活指数



    fun getViewType(context: Context): View {
        return when (this) {
            TODAY_DESC -> TodayDescLayout(context)
            NOW_WEATHER -> NowWeatherInfoCard(context)
            HOUR_WEATHER -> HourlyCardLayout(context)
            DAY_WEATHER -> DailyWeatherLayout(context)
            SUNRISE -> SunriseCardLayout(context)
            AIR -> AirCardLayout(context)
            TIPS -> TipsLayout(context)
        }
    }

}


