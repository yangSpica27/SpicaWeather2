package me.spica.spicaweather2.view.weather_detail_card

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp


class TempProgressView : View {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  var maxValue = 0

  var minValue = 0

  var currentMaxValue = 0

  var currentMinValue = 0

  private val progressHeight = 20.dp

  private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeCap = Paint.Cap.BUTT
    strokeJoin = Paint.Join.ROUND
    strokeWidth = progressHeight
    style = Paint.Style.FILL
    color = context.getColor(R.color.line_divider)
  }

  private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeCap = Paint.Cap.BUTT
    strokeJoin = Paint.Join.ROUND
    strokeWidth = progressHeight
    style = Paint.Style.FILL
  }

  fun init() {
    val startColor = if (minValue < 0) {
      context.getColor(R.color.light_blue_900)
    } else if (minValue < 10) {
      context.getColor(R.color.light_blue_600)
    } else {
      context.getColor(R.color.light_blue_200)
    }

    val endColor = if (maxValue in 10..19) {
      context.getColor(R.color.l6)
    } else if (minValue >= 30) {
      context.getColor(R.color.l8)
    } else {
      context.getColor(R.color.l3)
    }

    val shader = LinearGradient(
      0F,
      0F,
      width * 1f,
      0f,
      startColor,
      endColor,
      Shader.TileMode.CLAMP
    )

    progressPaint.shader = shader
    postInvalidate()
  }


  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawLine(0f, 0f, width * 1f, 0f, bgPaint)
    val startX = (currentMinValue - minValue)*1f / (maxValue - minValue) * (width * 1f)
    val endX = width-((maxValue - currentMaxValue)*1f / (maxValue - minValue) * (width * 1f))
    canvas.drawLine(startX , 0f , endX, 0f, progressPaint)
  }


  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    invalidate()
  }

}