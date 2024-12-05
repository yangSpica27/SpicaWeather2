package me.spica.spicaweather2.common

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.util.lruCache
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.weather_bg.NowWeatherView


// 天气类型
enum class WeatherType {
  WEATHER_SUNNY,
  WEATHER_CLOUDY,
  WEATHER_FORECAST,
  WEATHER_RAINY,
  WEATHER_SNOW,
  WEATHER_SLEET,
  WEATHER_FOG,
  WEATHER_HAZE,
  WEATHER_HAIL,
  WEATHER_THUNDER,
  WEATHER_THUNDERSTORM,
  WEATHER_SANDSTORM,
}

// 拓展方法 用于获取对应类型的图标
@DrawableRes
fun WeatherType.getIconRes(): Int =
  when (this) {
    WeatherType.WEATHER_SUNNY -> R.drawable.ic_sunny
    WeatherType.WEATHER_CLOUDY -> R.drawable.ic_cloudly
    WeatherType.WEATHER_FORECAST -> R.drawable.ic_forecast
    WeatherType.WEATHER_RAINY -> R.drawable.ic_rain
    WeatherType.WEATHER_SNOW -> R.drawable.ic_snow
    WeatherType.WEATHER_SLEET -> R.drawable.ic_rain
    WeatherType.WEATHER_FOG -> R.drawable.ic_fog
    WeatherType.WEATHER_HAZE -> R.drawable.ic_fog
    WeatherType.WEATHER_HAIL -> R.drawable.ic_rain
    WeatherType.WEATHER_THUNDER -> R.drawable.ic_thumb
    WeatherType.WEATHER_THUNDERSTORM -> R.drawable.ic_thumb
    WeatherType.WEATHER_SANDSTORM -> R.drawable.ic_storm_icon
  }

// // 拓展方法 用于获取对应类型的动画
// @RawRes
// fun WeatherType.getAnimRes(): Int {
//    return when (this) {
//        WeatherType.WEATHER_SUNNY -> R.raw.sunny
//        WeatherType.WEATHER_CLOUDY -> R.raw.windy
//        WeatherType.WEATHER_CLOUD -> R.raw.foggy
//        WeatherType.WEATHER_RAINY -> R.raw.shower
//        WeatherType.WEATHER_SNOW -> R.raw.snow
//        WeatherType.WEATHER_SLEET -> R.raw.shower
//        WeatherType.WEATHER_FOG -> R.raw.foggy
//        WeatherType.WEATHER_HAZE -> R.raw.mist
//        WeatherType.WEATHER_HAIL -> R.raw.shower
//        WeatherType.WEATHER_THUNDER -> R.raw.storm
//        WeatherType.WEATHER_THUNDERSTORM -> R.raw.storm
//    }
// }

// 拓展方法 用于获取对应类型的颜色
@ColorInt
fun WeatherType.getThemeColor(): Int =
  when (this) {
    WeatherType.WEATHER_SUNNY -> Color.parseColor("#FFfdbc4c")
    WeatherType.WEATHER_CLOUDY -> Color.parseColor("#4297e7")
    WeatherType.WEATHER_FORECAST -> Color.parseColor("#68baff")
    WeatherType.WEATHER_RAINY -> Color.parseColor("#4297e7")
    WeatherType.WEATHER_THUNDER -> Color.parseColor("#B296BD")
    WeatherType.WEATHER_FOG -> Color.parseColor("#757F9A")
    WeatherType.WEATHER_HAZE -> Color.parseColor("#E1C899")
    WeatherType.WEATHER_SNOW -> Color.parseColor("#FF4297e7")
    WeatherType.WEATHER_SLEET -> Color.parseColor("#FF00a5d9")
    WeatherType.WEATHER_SANDSTORM -> Color.parseColor("#FFE1C899")
    WeatherType.WEATHER_HAIL -> Color.parseColor("#FFE1C899")
    WeatherType.WEATHER_THUNDERSTORM -> Color.parseColor("#FF4A4646")
  }

private val bitmapCache =
  lruCache<WeatherType, Bitmap>(
    20 * 1024 * 1024,
  )


// 获取天气主题背景色Drawable
fun WeatherType.getDrawable(): GradientDrawable =
  when (this) {
    WeatherType.WEATHER_SUNNY ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#fdbc4c"),
          Color.parseColor("#ff8300"),
        ),
      )

    WeatherType.WEATHER_CLOUDY ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#4297e7"),
          Color.parseColor("#7F9CEA"),
        ),
      )

    WeatherType.WEATHER_FORECAST ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#68baff"),
          Color.parseColor("#A7B9EB"),
        ),
      )

    WeatherType.WEATHER_RAINY ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#4297e7"),
          Color.parseColor("#3a4d80"),
        ),
      )

    WeatherType.WEATHER_SNOW ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#4297e7"),
          Color.parseColor("#3a4d80"),
        ),
      )

    WeatherType.WEATHER_SLEET ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#00a5d9"),
          Color.parseColor("#1762ac"),
        ),
      )

    WeatherType.WEATHER_FOG ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#D7DDE8"),
          Color.parseColor("#757F9A"),
        ),
      )

    WeatherType.WEATHER_HAZE ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#E1C899"),
          Color.parseColor("#8CA2A5"),
        ),
      )

    WeatherType.WEATHER_HAIL ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#E1C899"),
          Color.parseColor("#8CA2A5"),
        ),
      )

    WeatherType.WEATHER_SANDSTORM ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#E1C899"),
          Color.parseColor("#8CA2A5"),
        ),
      )

    WeatherType.WEATHER_THUNDER ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#B296BD"),
          Color.parseColor("#50367F"),
        ),
      )

    WeatherType.WEATHER_THUNDERSTORM ->
      GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
          Color.parseColor("#B296BD"),
          Color.parseColor("#50367F"),
        ),
      )
  }

// // 获取天气主题背景色Shader
// fun WeatherType.getShader(context: Context): LinearGradient {
//    return when (this) {
//        WeatherType.WEATHER_SUNNY ->
//            LinearGradient(
//                0f, 0f, 0f, context.getScreenHeight() * 1f,
//                Color.parseColor("#fdbc4c"),
//                Color.parseColor("#ff8300"),
//                Shader.TileMode.CLAMP
//            )
//
//        WeatherType.WEATHER_CLOUDY -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f,
//            Color.parseColor("#4297e7"),
//            Color.parseColor("#7F9CEA"), Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_CLOUD -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f,
//            Color.parseColor("#68baff"),
//            Color.parseColor("#A7B9EB"),
//            Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_RAINY -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f, Color.parseColor("#4297e7"),
//            Color.parseColor("#3a4d80"), Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_SNOW -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f,
//            Color.parseColor("#68baff"),
//            Color.parseColor("#225fb6"), Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_SLEET -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f, Color.parseColor("#00a5d9"),
//            Color.parseColor("#1762ac"), Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_FOG -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f, Color.parseColor("#D7DDE8"),
//            Color.parseColor("#757F9A"), Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_HAZE -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f, Color.parseColor("#E1C899"),
//            Color.parseColor("#8CA2A5"), Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_HAIL -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f, Color.parseColor("#E1C899"),
//            Color.parseColor("#8CA2A5"), Shader.TileMode.CLAMP
//        )
//
//
//        WeatherType.WEATHER_THUNDER -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f, Color.parseColor("#B296BD"),
//            Color.parseColor("#50367F"), Shader.TileMode.CLAMP
//        )
//
//        WeatherType.WEATHER_THUNDERSTORM -> LinearGradient(
//            0f, 0f, 0f, context.getScreenHeight() * 1f, Color.parseColor("#B296BD"),
//            Color.parseColor("#50367F"), Shader.TileMode.CLAMP
//        )
//
//    }
// }

// 获取天气主题动画类型
fun WeatherType.getWeatherAnimType(): NowWeatherView.WeatherAnimType =
  when (this) {
    WeatherType.WEATHER_SUNNY -> NowWeatherView.WeatherAnimType.SUNNY
    WeatherType.WEATHER_CLOUDY -> NowWeatherView.WeatherAnimType.CLOUDY
    WeatherType.WEATHER_FORECAST -> NowWeatherView.WeatherAnimType.CLOUDY
    WeatherType.WEATHER_RAINY -> NowWeatherView.WeatherAnimType.RAIN
    WeatherType.WEATHER_SNOW -> NowWeatherView.WeatherAnimType.SNOW
    WeatherType.WEATHER_SLEET -> NowWeatherView.WeatherAnimType.RAIN
    WeatherType.WEATHER_FOG -> NowWeatherView.WeatherAnimType.HAZE
    WeatherType.WEATHER_HAZE -> NowWeatherView.WeatherAnimType.HAZE
    WeatherType.WEATHER_HAIL -> NowWeatherView.WeatherAnimType.RAIN
    WeatherType.WEATHER_THUNDER -> NowWeatherView.WeatherAnimType.RAIN
    WeatherType.WEATHER_THUNDERSTORM -> NowWeatherView.WeatherAnimType.RAIN
    WeatherType.WEATHER_SANDSTORM -> NowWeatherView.WeatherAnimType.SANDSTORM
  }
