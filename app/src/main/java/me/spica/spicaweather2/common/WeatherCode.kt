package me.spica.spicaweather2.common

object WeatherCodeUtils {

    fun getWeatherCode(iconId: Int): WeatherType {
        when (iconId.toString()) {

            "100" -> {
                return WeatherType.WEATHER_SUNNY
            }
            "101", "151", "153", "103" -> {
                return WeatherType.WEATHER_CLOUDY
            }
            "102", "104", "152", "154" -> {
                return WeatherType.WEATHER_CLOUD
            }

            "300", "301", "303", "305", "306",
            "307", "308", "309", "310", "311", "312",
            "314", "315", "316", "317", "318", "350", "351",
            "399"
            -> {
                return WeatherType.WEATHER_RAINY
            }

            "400", "401", "402", "403", "408", "409", "410",
            -> {
                return WeatherType.WEATHER_SNOW
            }
            "404", "405", "406", "456", "457", "499" -> {
                return WeatherType.WEATHER_SLEET
            }
            "500", "501", "502" -> {
                return WeatherType.WEATHER_FOG
            }
            "503", "504", "505", "506", "507", "508", "509", "510",

            "511", "512", "513", "514", "515" -> {
                WeatherType.WEATHER_HAZE
            }
            "313" -> {
                WeatherType.WEATHER_HAIL
            }
            "" -> {
                WeatherType.WEATHER_THUNDER
            }

            "302", "304" -> {
                WeatherType.WEATHER_THUNDERSTORM
            }
        }

        return WeatherType.WEATHER_SUNNY
    }
}
