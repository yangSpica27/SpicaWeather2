package me.spica.weather.weather_drawable

import android.graphics.Canvas

abstract class WeatherDrawable {


    abstract fun doOnDraw(canvas: Canvas, width: Int, height: Int)

}