package me.spica.spicaweather2.tools


fun getColorWithAlpha(alpha: Float, baseColor: Int): Int {
  val a = Math.min(255, Math.max(0, (alpha * 255).toInt())) shl 24
  val rgb = 0x00ffffff and baseColor
  return a + rgb
}