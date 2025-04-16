package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.toColorInt
import me.spica.spicaweather2.tools.dp
import timber.log.Timber
import kotlin.random.Random

private val random = Random.Default

class SandStormDrawable : WeatherDrawable() {
  private val paint =
    Paint().apply {
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val stormLines = ArrayList<StormLine>()

  override fun ready(
    width: Int,
    height: Int,
  ) {
    viewWidth = width
    viewHeight = height
    synchronized(this) {
      stormLines.clear()
      for (i: Int in 0..40) {
        stormLines.add(StormLine(width, height))
      }
    }
    Timber.tag("SandStormDrawable").e("stormLines size: ${stormLines.size}")
  }

  private var isPlus = true

  override fun calculate(
    width: Int,
    height: Int,
  ) {
    super.calculate(width, height)
    synchronized(this) {
      stormLines.forEach {
        it.next()
      }
    }

    if (isPlus) {
      extraAngle += 15 / 10f / 60f
    } else {
      extraAngle -= 15 / 10f / 60f
    }

    if (extraAngle > 15) {
      isPlus = false
    } else if (extraAngle < -15) {
      isPlus = true
    }
  }

  private var viewWidth: Int = -1

  private var viewHeight: Int = -1

  private var extraAngle = 0.0f

  override fun startAnim() {
    ready(viewWidth, viewHeight)
  }

  override fun cancelAnim() {
    super.cancelAnim()
    stormLines.clear()
  }

  override fun doOnDraw(
    canvas: Canvas,
    width: Int,
    height: Int,
  ) {
    canvas.save()
    canvas.rotate(extraAngle, width / 2f, height / 2f)
    synchronized(this) {
      stormLines.forEach {
        it.draw(canvas, paint)
      }
    }
    canvas.restore()
  }
}

data class StormLine(
  val width: Int,
  val height: Int,
) {
  private var cx: Float = 0.0f

  private var cy: Float = 0.0f

  private var radius: Float

  private val color: Int

  private val x: Float

  private val y: Float

  private val angle: Float

  private val speed: Float

  init {
    val rd = random.nextDouble()
    color =
      if (rd < 0.5) {
        "#333333".toColorInt()
      } else if (rd < 0.7) {
        "#FF795548".toColorInt()
      } else {
        "#bfbfbf".toColorInt()
      }

    radius = (2.dp + random.nextDouble() * 2.dp).toFloat()
    x = -width * random.nextDouble().toFloat() - width
    y = (random.nextDouble() * height / 10f * 8f + height / 10f).toFloat()
    angle = ((y - height / 10f) / (height / 10f * 8f) * 15 - 7.5).toFloat()
    speed = (5.0 + random.nextDouble() * 5f).toFloat()
    cx = x
    cy = y
  }

  fun next() {
    cx += speed
    if (cx > (width * 2)) {
      cx = x
    }
  }

  fun draw(
    canvas: Canvas,
    paint: Paint,
  ) {
    paint.color = color
    paint.strokeWidth = radius

    canvas.rotate(angle)

    for (i: Int in 0..5) {
      canvas.drawCircle(
        cx + 3 * i * radius,
        cy,
        radius + radius / 2 * ((cx + 3 * i * radius) / width),
        paint,
      )
    }

    canvas.rotate(-angle)
  }
}
