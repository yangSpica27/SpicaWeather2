package me.spica.spicaweather2.tools

import android.graphics.Color

/**
 * 获取对应的颜色的不同透明度的颜色
 * @param alpha 透明度
 * @param baseColor 基础颜色
 */
fun getColorWithAlpha(
  alpha: Float,
  baseColor: Int,
): Int {
  var alpha1 = alpha
  if (alpha1 < 0) {
    alpha1 = 0f
  } else if (alpha1 > 1f) {
    alpha1 = 1f
  }
  val a = Math.min(255, Math.max(0, (alpha1 * 255).toInt())) shl 24
  val rgb = 0x00ffffff and baseColor
  return a + rgb
}

/**
 * 获取变化中间色
 * @param color1 颜色1
 * @param color2 颜色2
 * @param ratio 比例
 */
fun blendColors(
  color1: Int,
  color2: Int,
  ratio: Float,
): Int {
  val inverseRatio = 1f - ratio
  val a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio)
  val r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio)
  val g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio)
  val b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio)
  return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}
