package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.WorkerThread
import me.spica.spicaweather2.render.RainPoint
import me.spica.spicaweather2.render.SnowEffectCounter
import me.spica.spicaweather2.tools.dp

class SnowDrawable : WeatherDrawable() {

    // 雪的集合
    private var snows: ArrayList<RainPoint> = arrayListOf()

    // 绘制雨水的paint
    private val snowPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4.dp
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val snowEffectCounter = SnowEffectCounter()

    fun ready(width: Int, height: Int) {
      synchronized(this){
          snows.clear()
          snowEffectCounter.init(width,height)
          for (i in 1..100){
              snows.add(RainPoint().apply {
                  this.width = 10.dp
                  snowEffectCounter.createParticle(this, isBg = i<=80)
              })
          }
      }
    }

    @WorkerThread
    fun calculate(width: Int, height: Int) {
        synchronized(this){
            snowEffectCounter.run()
        }
    }

    private val drawList = arrayListOf<Float>()

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(this){
            drawList.clear()
            for (rain in snows) {
                snowEffectCounter.getXy(rain)
                rain.getPoint().let {
                    drawList.add(it[0])
                    drawList.add(it[1])
                }
                snowPaint.strokeWidth = rain.width
            }
            canvas.drawPoints(drawList.toFloatArray(), snowPaint)
        }
    }

    fun setBackgroundY(y: Int) {
        snowEffectCounter.setBackgroundY(y)
    }


}
