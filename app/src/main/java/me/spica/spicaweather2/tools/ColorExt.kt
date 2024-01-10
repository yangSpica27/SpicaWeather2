package me.spica.spicaweather2.tools

/**
 * 获取对应的颜色的不同透明度的颜色
 * @param alpha 透明度
 * @param baseColor 基础颜色
 */
fun getColorWithAlpha(alpha: Float, baseColor: Int): Int {
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
