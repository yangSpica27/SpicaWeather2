package me.spica.spicaweather2.view.weather_detail_card

enum class HomeCardType(val code: Int) {
    NOW_WEATHER(0),// 现在的天气
//    TODAY_EXTRA(1),// 生活指数
    HOUR_WEATHER(2), // 小时天气
    DAY_WEATHER(3), // 日级天气
    SUNRISE(4),// 日出日落
    AIR(5),
    TIPS(6);// 生活指数
}