package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.WorkerThread
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.weather_anim_counter.RainOrSnowPoint
import me.spica.spicaweather2.weather_anim_counter.SnowEffectCounter

/**
 * 雪天的天气效果
 */
class SnowDrawable : WeatherDrawable() {
  // 雪的集合
  private var snows: ArrayList<RainOrSnowPoint> = arrayListOf()

  // 绘制雪的paint
  private val snowPaint =
    Paint().apply {
      strokeCap = Paint.Cap.ROUND
      strokeWidth = 2.dp
      color = "#14000000".toColorInt()
      style = Paint.Style.FILL
    }

  private val snowEffectCounter = SnowEffectCounter()


  override fun applyLinearImpulse(x: Float, y: Float) {
    snowEffectCounter.applyLinearImpulse(x, y)
  }


  private val colors =
    intArrayOf(
      Color.WHITE,
      ColorUtils.setAlphaComponent(Color.WHITE,200),
      ColorUtils.setAlphaComponent(Color.WHITE,150),
    )

  override fun startAnim() = Unit

  override fun cancelAnim() = Unit

  override fun ready(
    width: Int,
    height: Int,
  ) {
    synchronized(this) {
      snows.clear()
      snowEffectCounter.init(width, height)
      for (i in 1..100) {
        snows.add(
          RainOrSnowPoint(
            color = colors[i % 3],
          ).apply {
            this.width = 6.dp
            snowEffectCounter.createParticle(this, isBg = i % 2 == 1)
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
      snowEffectCounter.run()
    }
  }

  override fun doOnDraw(
    canvas: Canvas,
    width: Int,
    height: Int,
  ) {
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
