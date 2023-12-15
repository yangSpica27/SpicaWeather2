package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.spica.spicaweather2.render.RainEffectRender
import me.spica.spicaweather2.render.RainPoint
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.weather_bg.RainFlake
import timber.log.Timber

class RainDrawable : WeatherDrawable() {

    // 绘制雨水的paint
    private val rainPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12.dp
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    // 雨水的合集
    private var rains: ArrayList<RainPoint> = arrayListOf()
    private var bgRains: ArrayList<RainFlake> = arrayListOf()
    private val rainEffectRender = RainEffectRender()

    private val lock = Any()

    fun ready(width: Int, height: Int) {
        synchronized(lock) {
            rainEffectRender.init(width, height)
            rains.clear()
            bgRains.clear()
            for (i in 0 until 150) {
                rains.add(
                    RainPoint().apply {
                        rainEffectRender.createParticle(this)
                    }
                )
            }
            for (i in 0 until 150) {
                bgRains.add(RainFlake.create(width, height, rainPaint))
            }
        }
    }

    fun setBackgroundY(y: Int) {
        rainEffectRender.setBackgroundY(y)
    }

    fun calculate(width: Int, height: Int) {
        synchronized(lock) {
            Timber.tag("rain").e("计算")
            bgRains.forEach {
                it.calculation(width, height)
            }
            rainEffectRender.run()
        }
    }


    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(lock) {
            Timber.tag("rain").e("绘制")
            val list = arrayListOf<Float>()
            rainPaint.color = Color.WHITE
            for (rain in rains) {
                rainEffectRender.getXy(rain)
                rain.getPoint().let {
                    list.add(it[0])
                    list.add(it[1])
                }
                rainPaint.strokeWidth = rain.width
            }
            canvas.drawPoints(list.toFloatArray(), rainPaint)

            rainPaint.color = Color.parseColor("#F8f8f8")

            bgRains.forEach {
                it.onlyDraw(canvas)
            }
        }
    }
}
