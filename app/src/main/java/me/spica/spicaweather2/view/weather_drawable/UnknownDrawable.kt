package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas

class UnknownDrawable : WeatherDrawable() {
  override fun startAnim() = Unit

  override fun cancelAnim() = Unit

  override fun ready(
    width: Int,
    height: Int,
  ) = Unit

  override fun doOnDraw(canvas: Canvas, width: Int, height: Int) = Unit
  override fun calculate(
    width: Int,
    height: Int,
  ) = Unit


  override fun setBackgroundY(y: Int) = Unit
}