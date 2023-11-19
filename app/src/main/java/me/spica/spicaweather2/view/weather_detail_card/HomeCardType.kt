package me.spica.spicaweather2.view.weather_detail_card

enum class HomeCardType(val code: Int) {
    NOW_WEATHER(0),// 现在的天气
    HOUR_WEATHER(1), // 小时天气
    DAY_WEATHER(2), // 日级天气
    SUNRISE(3),// 日出日落
    AIR(4),
    TIPS(5);// 生活指数

}
fun Int.toHomeCardType(): HomeCardType {
    return when (this) {
        HomeCardType.NOW_WEATHER.code -> HomeCardType.NOW_WEATHER
        HomeCardType.HOUR_WEATHER.code -> HomeCardType.HOUR_WEATHER
        HomeCardType.DAY_WEATHER.code -> HomeCardType.DAY_WEATHER
        HomeCardType.SUNRISE.code -> HomeCardType.SUNRISE
        HomeCardType.AIR.code -> HomeCardType.AIR
        HomeCardType.TIPS.code -> HomeCardType.TIPS
        else -> HomeCardType.NOW_WEATHER
    }
}