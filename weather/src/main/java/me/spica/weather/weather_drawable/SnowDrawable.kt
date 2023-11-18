package me.spica.weather.weather_drawable


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.WorkerThread
import me.spica.base.tools.dp

class SnowDrawable : WeatherDrawable() {

    // 雪的集合
    private var snows: ArrayList<SnowFlake> = arrayListOf()


    // 绘制雨水的paint
    private val snowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4.dp
        color = Color.WHITE
        style = Paint.Style.FILL
    }

  fun ready(width: Int, height: Int) {
      synchronized(snows){
          snows.clear()
          for (i in 0 until 30) {
              snows.add(SnowFlake.create(width, height, snowPaint))
          }
      }
    }


    @WorkerThread
    fun calculate(width: Int, height: Int) {
      synchronized(snows){
          snows.forEach { snow ->
              snow.onlyCalculation(width, height)
          }
      }
    }


    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
       synchronized(snows){
           snows.forEach { snow ->
               snow.onlyDraw(canvas)
           }
       }
    }


}