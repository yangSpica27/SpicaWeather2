package me.spica.weather.weather_drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import me.spica.weather.R
import kotlin.random.Random

private const val ALPHA_MAX = 200

private const val ALPHA_MIN = 165

private const val MAX_SPEED = 2

private const val MIN_SPEED = .2

private const val MAX_RADIUS = 20

private const val MIN_RADIUS = 60

class HazeDrawable(private val context: Context) : WeatherDrawable() {

    private var width = 0

    private var height = 0

    private val random: Random = Random.Default

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)


    private val points: MutableList<Particle> = arrayListOf()


    fun ready(width: Int, height: Int) {
      synchronized(points){
          this.width = width
          this.height = height
          points.clear()
          for (index in 0..60) {
              points.add(
                  Particle(
                      if (index % 2 == 0) {
                          ContextCompat.getColor(context, R.color.fog_color)
                      } else {
                          ContextCompat.getColor(context, R.color.fog_color2)
                      }
                  )
              )
          }
      }
    }

    fun calculate() {
      synchronized(points){
          points.forEach {
              it.calculate()
          }
      }
    }

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(points) {
            points.forEach {
                it.draw(canvas)
            }
        }
    }


    private inner class Particle(
        @ColorInt private val color: Int,
        private val paint: Paint = pointPaint,
    ) {


        private var alpha = 0

        private var currentX: Float = 0f

        private var currentMaxRadius = 0

        private var radius = 0

        private var currentY: Float = 0f

        private var speedX: Float = 0f

        private var speedY: Float = 0f


        init {
            init()
        }

        private fun init() {
            currentX = (Math.random() * width).toFloat()
            currentY = (Math.random() * height).toFloat()
            alpha = (Math.random() * (ALPHA_MAX - ALPHA_MIN) + ALPHA_MIN).toInt()
            speedX = if (random.nextBoolean()) (
                -(Math.random() * (MAX_SPEED - MIN_SPEED) + MAX_SPEED).toFloat()
                ) else ((Math.random() * (MAX_SPEED - MIN_SPEED) + MAX_SPEED).toFloat())
            speedY = if (random.nextBoolean()) {
                -(Math.random() * (MAX_SPEED - MIN_SPEED) + MAX_SPEED).toFloat()
            } else {
                (Math.random() * (MAX_SPEED - MIN_SPEED) + MAX_SPEED).toFloat()
            }
            currentMaxRadius = (Math.random() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS).toInt()
            radius = 0
        }

        fun calculate() {
            currentY += speedY
            currentX += speedX
            if (radius < currentMaxRadius) {
                radius += (currentMaxRadius / 120f).toInt()
            }
            if ((currentX + currentMaxRadius > width || currentX - currentMaxRadius < 0) && (currentY + currentMaxRadius > height || currentY - currentMaxRadius < 0)) {
                init()
            }
        }

        fun draw(canvas: Canvas) {
            paint.alpha = alpha
            paint.color = color
            paint.style = Paint.Style.FILL
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeWidth = currentMaxRadius * 1f
            canvas.drawPoint(
                currentX, currentY, paint
            )
        }
    }


}