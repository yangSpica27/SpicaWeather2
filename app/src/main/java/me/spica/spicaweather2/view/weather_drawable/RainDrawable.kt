package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.weather_bg.RainFlake
import me.spica.spicaweather2.weather_anim_counter.RainEffectCounter
import me.spica.spicaweather2.weather_anim_counter.RainOrSnowPoint

class RainDrawable : WeatherDrawable() {

    // 绘制雨水的paint
    private val rainPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12.dp
        color = Color.WHITE
        style = Paint.Style.FILL
    }


    // 雨水的合集
    private var rains: ArrayList<RainOrSnowPoint> = arrayListOf()
    private var bgRains: ArrayList<RainFlake> = arrayListOf()
    private val rainEffectCounter = RainEffectCounter()

    private val lock = Any()

    private val colors = intArrayOf(
        Color.parseColor("#FFECECEC"),
        Color.parseColor("#E6FFFFFF"),
        Color.WHITE
    )

    fun ready(width: Int, height: Int) {
        synchronized(lock) {
            rainEffectCounter.init(width, height)
            rains.clear()
            bgRains.clear()
            for (i in 0 until 150) {
                rains.add(
                    RainOrSnowPoint(
                        colors[i % colors.size],
                    ).apply {
                        rainEffectCounter.createParticle(this)
                    }
                )
            }
            for (i in 0 until 150) {
                bgRains.add(RainFlake.create(width, height, rainPaint, colors[i % colors.size]))
            }
        }
    }

    fun setBackgroundY(y: Int) {
        rainEffectCounter.setBackgroundY(y)
    }

    fun calculate(width: Int, height: Int) {
        synchronized(lock) {
            bgRains.forEach {
                it.calculation(width, height)
            }
            rainEffectCounter.run()
        }
    }

    private val drawList = arrayListOf<Float>()

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(lock) {
            drawList.clear()
            rainPaint.color = Color.WHITE
            for (rain in rains) {
                rainEffectCounter.getXy(rain)
                rain.getPoint().let {
                    drawList.add(it[0])
                    drawList.add(it[1])
                }
                rainPaint.strokeWidth = rain.width
            }
            canvas.drawPoints(drawList.toFloatArray(), rainPaint)

            bgRains.forEach {
                it.onlyDraw(canvas)
            }
        }
    }


}
