package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import me.spica.spicaweather2.tools.dp

// 描述卡片下面的条
class DescProgressLineView : View {
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

  constructor(context: Context?) : super(context)

  private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    color = "#1B000000".toColorInt()
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 6.dp
  }

  private val sunrisePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    color = Color.parseColor("#FFE5E5E5")
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 6.dp
  }

  private val waterShaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 6.dp
  }

  private val uvShaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 8.dp
  }

  private val uvShaderPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 10.dp
    color = Color.WHITE
  }

  private val uvPointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 6.dp
  }

  private val feelTempPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 8.dp
  }

  private val feelTempPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 6.dp
  }

  private var progress = 1f

  // 1:紫外线 2：湿度 3：体感温度 4：日出日落
  private var mode = 1

  fun setProgress(
    progress: Float,
    mode: Int,
  ) {
    this.progress = progress
    this.mode = mode
    invalidate()
  }

  init {
    setPadding(12.dp.toInt(), 12.dp.toInt(), 12.dp.toInt(), 12.dp.toInt())
    ViewCompat.setElevation(this, 2.dp)
  }

  override fun onSizeChanged(
    w: Int,
    h: Int,
    oldw: Int,
    oldh: Int,
  ) {
    super.onSizeChanged(w, h, oldw, oldh)
    if (w != oldw) {
      waterShaderPaint.shader = LinearGradient(
        0f,
        0f,
        w.toFloat(),
        0f,
        intArrayOf(
          Color.parseColor("#FFB3E5FC"),
          Color.parseColor("#FF81D4FA"),
          Color.parseColor("#FF29B6F6"),
          Color.parseColor("#FF42A5F5"),
        ),
        floatArrayOf(
          0f,
          0.5f,
          0.7f,
          1f,
        ),
        Shader.TileMode.CLAMP,
      )
      uvShaderPaint.shader = LinearGradient(
        0f,
        0f,
        w.toFloat(),
        0f,
        intArrayOf(
          Color.parseColor("#FF66BB6A"),
          Color.parseColor("#FFFFEB3B"),
          Color.parseColor("#FFFFAB40"),
          Color.parseColor("#FFEF5350"),
          Color.parseColor("#FFAB47BC"),
        ),
        floatArrayOf(
          0.15f,
          0.3f,
          0.6f,
          0.85f,
          1f,
        ),
        Shader.TileMode.CLAMP,
      )
    }
  }

  private val feelTempColors = intArrayOf(
    Color.parseColor("#FF42A5F5"),
    Color.parseColor("#FF66BB6A"),
    Color.parseColor("#FFFF9100"),
  )

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    setMeasuredDimension(
      MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(
        12.dp.toInt(),
        heightMeasureSpec,
      ),
    )
  }

  private val path = Path()

  private val pathMeasure = PathMeasure()

  private val pos = floatArrayOf(0f, 0f)

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    // 1:紫外线 2：湿度 3：体感温度 4：日出日落
    if (mode == 1) {

      canvas.drawLine(
        paddingLeft * 1f,
        height - uvShaderPaint.strokeWidth - paddingBottom,
        width.toFloat() - paddingRight,
        height - uvShaderPaint.strokeWidth - paddingBottom,
        uvShaderPaint2,
      )

      canvas.drawLine(
        paddingLeft * 1f,
        height - uvShaderPaint.strokeWidth - paddingBottom,
        width.toFloat() - paddingRight,
        height - uvShaderPaint.strokeWidth - paddingBottom,
        uvShaderPaint,
      )

      uvPointPaint.color = Color.WHITE
      uvPointPaint.strokeWidth = 18.dp
      canvas.drawPoint(
        ((width.toFloat() - paddingRight) * progress).coerceIn(
          paddingLeft.toFloat() + uvPointPaint.strokeWidth / 2f,
          (width.toFloat() - paddingRight) - uvPointPaint.strokeWidth / 2f,
        ),
        height - uvShaderPaint.strokeWidth - paddingBottom,
        uvPointPaint,
      )

      uvPointPaint.color = if (progress < 0.15f) {
        Color.parseColor("#FF66BB6A")
      } else {
        if (progress < 0.3f) {
          Color.parseColor("#FFFFEB3B")
        } else {
          if (progress < 0.6f) {
            Color.parseColor("#FFFFAB40")
          } else {
            if (progress < 0.85f) {
              Color.parseColor("#FFEF5350")
            } else {
              Color.parseColor("#FFAB47BC")
            }
          }
        }
      }
      uvPointPaint.strokeWidth = 14.dp
      canvas.drawPoint(
        ((width.toFloat() - paddingRight) * progress).coerceIn(
          paddingLeft.toFloat() + 18.dp / 2f,
          (width.toFloat() - paddingRight) - 18.dp / 2f,
        ),
        height - uvShaderPaint.strokeWidth - paddingBottom,
        uvPointPaint,
      )
    } else {
      if (mode == 2) {
        canvas.drawLine(
          paddingLeft * 1f,
          height - waterShaderPaint.strokeWidth - paddingBottom,
          (width.toFloat() - paddingRight),
          height - waterShaderPaint.strokeWidth - paddingBottom,
          bgPaint.apply {
            strokeWidth = 10.dp
          },
        )
        canvas.drawLine(
          paddingLeft * 1f,
          height - waterShaderPaint.strokeWidth - paddingBottom,
          (width.toFloat() - paddingRight) * progress,
          height - waterShaderPaint.strokeWidth - paddingBottom,
          waterShaderPaint,
        )
      } else {
        if (mode == 3) {
          feelTempPaint.color = "#1B000000".toColorInt()
          feelTempPaint.strokeWidth = 20.dp
          val sx = paddingLeft * 1f + feelTempPaint.strokeWidth / 2
          val ex = width.toFloat() - paddingRight - feelTempPaint.strokeWidth / 2
          canvas.drawLine(
            sx,
            height - feelTempPaint.strokeWidth - paddingBottom,
            ex,
            height - feelTempPaint.strokeWidth - paddingBottom,
            feelTempPaint,
          )

          feelTempPaint2.color = "#A6FFFFFF".toColorInt()
          feelTempPaint2.strokeWidth = 2.dp
          for (index in 1..30) {
            val fraction = index / 30f
            val x = sx + (ex - sx) * fraction



            val lineHeight = if (index == 13 || index == 19) {
              feelTempPaint2.strokeWidth = 8f
              5.dp
            } else {
              feelTempPaint2.strokeWidth = 4f
              3.dp
            }
            canvas.drawLine(
              x,
              height - feelTempPaint.strokeWidth - paddingBottom - lineHeight,
              x,
              height - feelTempPaint.strokeWidth - paddingBottom + lineHeight, feelTempPaint2
            )
          }
          feelTempPaint2.strokeWidth = 4.dp
          canvas.drawLine(
            sx + (ex - sx) / 30f,
            height - feelTempPaint.strokeWidth - paddingBottom,
            ex,
            height - feelTempPaint.strokeWidth - paddingBottom,
            feelTempPaint2,
          )

          feelTempPaint2.color = "#A6FFFFFF".toColorInt()
          feelTempPaint2.strokeWidth = 12.dp
          canvas.drawLine(
            sx,
            height - feelTempPaint.strokeWidth - paddingBottom,
            sx + (ex - sx) * progress,
            height - feelTempPaint.strokeWidth - paddingBottom,
            feelTempPaint2,
          )


        } else if (mode == 4) {
          sunrisePaint.strokeWidth = 6.dp
          sunrisePaint.color = "#1B000000".toColorInt()

          canvas.drawArc(
            paddingLeft * 1f,
            paddingTop * 1f,
            width.toFloat() - paddingRight,
            height.toFloat() - sunrisePaint.strokeWidth,
            270f - 135 / 2f,
            135f,
            false,
            sunrisePaint,
          )
          sunrisePaint.color = "#1B000000".toColorInt()
          // 获取对应progress的角度
          val progressAngle = 135 * progress

          path.reset()
          path.addArc(
            paddingLeft * 1f,
            paddingTop * 1f,
            width.toFloat() - paddingRight,
            height.toFloat() - sunrisePaint.strokeWidth,
            270f - 135 / 2f,
            progressAngle,
          )
          canvas.drawPath(path, sunrisePaint)

          pathMeasure.setPath(path, false)

          if (progress == 0f || progress == 1f) {
            return
          }

          // 绘制日出日落的点
          pathMeasure.getPosTan(
            pathMeasure.length,
            pos,
            floatArrayOf(0f, 0f),
          )
          sunrisePaint.strokeWidth = 25.dp
          sunrisePaint.color = "#FFD9D9D9".toColorInt()
          canvas.drawPoint(pos[0], pos[1], sunrisePaint)
          sunrisePaint.strokeWidth = 18.dp
          sunrisePaint.color = "#FF999999".toColorInt()
          canvas.drawPoint(pos[0], pos[1], sunrisePaint)
        }
      }
    }
  }
}
