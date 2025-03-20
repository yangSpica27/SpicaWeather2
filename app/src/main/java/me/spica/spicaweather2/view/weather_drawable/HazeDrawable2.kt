package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.WorkerThread
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.weather_anim_counter.HazeParticleManager
import me.spica.spicaweather2.weather_anim_counter.RainOrSnowPoint

class HazeDrawable2 : WeatherDrawable() {


  // 尘埃
  private var points: ArrayList<RainOrSnowPoint> = arrayListOf()

  // 绘制尘埃的paint
  private val paint =
    Paint().apply {
      strokeCap = Paint.Cap.ROUND
      strokeWidth = 2.dp
      color = Color.WHITE
      style = Paint.Style.FILL
    }

  private val counter = HazeParticleManager()

  private val colors =
    intArrayOf(
      Color.parseColor("#26787474"),
      Color.parseColor("#ff9A9A9A"),
      Color.parseColor("#ff999999"),
    )

  override fun startAnim() = Unit

  override fun cancelAnim() = Unit

  override fun ready(
    width: Int,
    height: Int,
  ) {
    synchronized(this) {
      points.clear()
      counter.init(width, height)
      for (i in 1..2000) {
        points.add(
          RainOrSnowPoint(
            color = colors[i % 3],
          ).apply {
            this.width = 2.dp
            counter.createParticle(this, isBg = i % 2 == 1)
          },
        )
      }
    }
  }

  @WorkerThread
  override fun calculate(
    width: Int,
    height: Int,
  ) {
    synchronized(this) {
      counter.run()
    }
  }

  override fun doOnDraw(
    canvas: Canvas,
    width: Int,
    height: Int,
  ) {
    synchronized(this) {
      for (item in points) {
        counter.getXy(item)
        item.getPoint().let {
          paint.strokeWidth = item.width
          paint.color = item.color
          canvas.drawPoint(it[0], it[1], paint)
        }
      }
    }
  }

  override fun setBackgroundY(y: Int) {
    counter.setBackgroundY(y)
  }
}