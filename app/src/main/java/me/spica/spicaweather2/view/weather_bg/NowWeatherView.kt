package me.spica.spicaweather2.view.weather_bg

/**
 * 目前的天气
 */
open class NowWeatherView {
    enum class WeatherAnimType {
        SUNNY, // 晴朗
        CLOUDY, // 多云
        RAIN, // 下雨
        SNOW, // 下雪
        FOG, // 雾天
        HAZE, // 霾天
        SANDSTORM, // 沙尘暴
        UNKNOWN, // 无效果
    }
}
