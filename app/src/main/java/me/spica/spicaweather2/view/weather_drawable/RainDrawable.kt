package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.spica.spicaweather2.render.RainEffectRender
import me.spica.spicaweather2.render.RainPoint
import me.spica.spicaweather2.tools.dp

class RainDrawable : WeatherDrawable() {

    // 绘制雨水的paint
    private val rainPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12.dp
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    // 雨水的合集
//    private var rains: ArrayList<RainFlake> = arrayListOf()
    private var rains: ArrayList<RainPoint> = arrayListOf()
    private val rainEffectRender = RainEffectRender()

    fun ready(width: Int, height: Int) {
        rainEffectRender.init(width, height)
        synchronized(rains) {
            rains.clear()
            for (i in 0 until 100) {
                rains.add(
                    RainPoint().apply {
                        rainEffectRender.createParticle(this)
                    }
                )
            }
        }
    }

    fun setBackgroundY(y: Int) {
        rainEffectRender.setBackgroundY(y)
    }

    fun calculate(width: Int, height: Int) {
        rainEffectRender.run()
    }

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(rains) {
            val list = arrayListOf<Float>()
            for (rain in rains) {
                rainEffectRender.getXy(rain)
                rain.getPoint().let {
                    list.add(it[0])
                    list.add(it[1])
                }
                rainPaint.strokeWidth = rain.width
            }
            canvas.drawPoints(list.toFloatArray(), rainPaint)
        }
//        synchronized(rains){
//            rains.forEach { rainFlake ->
//                rainFlake.draw(canvas)
//            }
//        }
    }
}
