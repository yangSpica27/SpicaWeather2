package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.WorkerThread
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.weather_anim_counter.RainOrSnowPoint
import me.spica.spicaweather2.weather_anim_counter.SnowEffectCounter

/**
 * 雪天的天气效果
 */
class SnowDrawable : WeatherDrawable() {

    // 雪的集合
    private var snows: ArrayList<RainOrSnowPoint> = arrayListOf()

    // 绘制雨水的paint
    private val snowPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4.dp
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val snowEffectCounter = SnowEffectCounter()

    private val colors = intArrayOf(
        Color.parseColor("#26787474"),
        Color.parseColor("#ff9A9A9A"),
        Color.WHITE
    )

    override fun startAnim() = Unit

    override fun cancelAnim() = Unit


    override fun ready(width: Int, height: Int) {
        synchronized(this) {
            snows.clear()
            snowEffectCounter.init(width, height)
            for (i in 1..100) {
                snows.add(RainOrSnowPoint(
                    color = colors[i % 3],
                ).apply {
                    this.width = 10.dp
                    snowEffectCounter.createParticle(this, isBg = true)
                })
            }
        }
    }

    @WorkerThread
    override fun calculate(width: Int, height: Int) {
        synchronized(this) {
            snowEffectCounter.run()
        }
    }


    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(this) {
            for (rain in snows) {
                snowEffectCounter.getXy(rain)
                rain.getPoint().let {
                    snowPaint.strokeWidth = rain.width
                    snowPaint.color = rain.color
                    canvas.drawPoint(it[0], it[1], snowPaint)
                }
            }
        }
    }

    override fun setBackgroundY(y: Int) {
        snowEffectCounter.setBackgroundY(y)
    }


}
