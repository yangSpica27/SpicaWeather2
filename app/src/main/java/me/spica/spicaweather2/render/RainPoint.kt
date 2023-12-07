package me.spica.spicaweather2.render

import android.graphics.Canvas

class RainPoint : BaseParticle() {

    val canvas = Canvas()
    fun getPoint() = floatArrayOf(x, y)
}
