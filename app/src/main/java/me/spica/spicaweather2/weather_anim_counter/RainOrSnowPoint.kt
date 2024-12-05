package me.spica.spicaweather2.weather_anim_counter

import android.graphics.Color

/**
 * 雪花/雨 粒子基类
 */
class RainOrSnowPoint(
  val color: Int = Color.WHITE,
) : BaseParticle() {
  fun getPoint() = floatArrayOf(x, y)
}
