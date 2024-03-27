package me.spica.spicaweather2.view.weather_detail_card

import android.content.Context
import android.view.View
import me.spica.spicaweather2.view.NowWeatherInfoCard
import me.spica.spicaweather2.view.view_group.AirCardLayout
import me.spica.spicaweather2.view.view_group.DailyWeatherLayout
import me.spica.spicaweather2.view.view_group.HourlyCardLayout
import me.spica.spicaweather2.view.view_group.MinuteWeatherCard
import me.spica.spicaweather2.view.view_group.TodayDescLayout
import me.spica.spicaweather2.view.view_group.SunriseCardLayout
import me.spica.spicaweather2.view.view_group.TipsLayout

enum class HomeCardType(val code: Int) {
    TODAY_DESC(1), // 今天的描述
    MINUTE_WEATHER(2), // 分钟级天气
    NOW_WEATHER(3), // 现在的天气
    HOUR_WEATHER(4), // 小时天气
    DAY_WEATHER(5), // 日级天气
    SUNRISE(6), // 日出日落
    AIR(7),
    TIPS(8); // 生活指数


    fun getViewType(context: Context): View {
        return when (this) {
            TODAY_DESC -> TodayDescLayout(context)
            MINUTE_WEATHER -> MinuteWeatherCard(context)
            NOW_WEATHER -> NowWeatherInfoCard(context)
            HOUR_WEATHER -> HourlyCardLayout(context)
            DAY_WEATHER -> DailyWeatherLayout(context)
            SUNRISE -> SunriseCardLayout(context)
            AIR -> AirCardLayout(context)
            TIPS -> TipsLayout(context)
        }
    }

}


