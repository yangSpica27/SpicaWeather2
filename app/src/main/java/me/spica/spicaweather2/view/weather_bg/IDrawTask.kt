package me.spica.spicaweather2.view.weather_bg

import android.graphics.Canvas

interface IDrawTask {
  fun ready()

  fun lockCanvas(): Canvas?

  fun draw(canvas: Canvas)

  fun unlockCanvas(canvas: Canvas?)

  fun destroy()
}
