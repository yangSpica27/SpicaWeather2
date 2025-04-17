package me.spica.spicaweather2.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp

// 空气质量指数view
private val VIEW_MARGIN = 14.dp

class AirCircleProgressView : View {
  private var mCenterX = 0
  private var mCenterY = 0

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  private val mRectF: RectF = RectF()

  private val textPaint =
    TextPaint().apply {
      textSize = 50.dp
      color = ContextCompat.getColor(context, R.color.white)
      isFakeBoldText = true
      typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Typeface.create(Typeface.DEFAULT_BOLD, 900, false)
      } else {
        Typeface.DEFAULT_BOLD
      }
    }

  private val secondTextPaint =
    TextPaint().apply {
      textSize = 15.dp
      color = ContextCompat.getColor(context, R.color.white)
      typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Typeface.create(Typeface.DEFAULT, 500, false)
      } else {
        Typeface.DEFAULT
      }
    }

  private val secondTextBackgroundPaint =
    TextPaint().apply {
      color = ContextCompat.getColor(context, R.color.white)
      style = Paint.Style.FILL
    }

  private val linePaint =
    Paint().apply {
      strokeWidth = 12.dp
      style = Paint.Style.STROKE
      color = "#1B000000".toColorInt()
      strokeCap = Paint.Cap.ROUND
      strokeJoin = Paint.Join.ROUND
    }
  private val dotLinePaint = Paint().apply {
    strokeWidth = 6.dp
    style = Paint.Style.STROKE
    color = "#FFFFFF".toColorInt()
    pathEffect = DashPathEffect(floatArrayOf(1.dp, 4.dp, 4.dp), 0f)
  }

  private val startAngle = 135f

  private val swipeAngle = 270f

  private var lv = 100

  private val maxLv = 400

  private var progress = 0f

  private var category = "良"

  // 设置进度
  fun bindProgress(
    lv: Int,
    category: String,
  ) {
    // 根据空气质量等级设置颜色
    val progressColor = when {
      lv < 50 -> ContextCompat.getColor(context, R.color.l2)
      lv < 100 -> ContextCompat.getColor(context, R.color.l4)
      lv < 150 -> ContextCompat.getColor(context, R.color.l5)
      lv < 200 -> ContextCompat.getColor(context, R.color.l6)
      lv < 300 -> ContextCompat.getColor(context, R.color.l7)
      else -> ContextCompat.getColor(context, R.color.l8)
    }
    secondTextBackgroundPaint.color = progressColor
    this.lv = lv
    this.category = category
    postInvalidateOnAnimation()
  }

  // 开始动画
  fun startAnim() {
    val animator = ValueAnimator.ofFloat(0f, lv * 1f / maxLv)
    animator.duration = 1500
    animator.addUpdateListener {
      progress = it.animatedValue as Float
      progress = Math.min(progress, 1f)
      postInvalidateOnAnimation()
    }
    animator.doOnEnd {
      it.removeAllListeners()
    }
    animator.start()
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val width = (MeasureSpec.getSize(widthMeasureSpec) - 2 * VIEW_MARGIN)
    setMeasuredDimension(
      MeasureSpec.makeMeasureSpec(
        (width + 2 * VIEW_MARGIN).toInt(),
        MeasureSpec.EXACTLY,
      ),
      MeasureSpec.makeMeasureSpec(
        (width + 2 * VIEW_MARGIN).toInt(),
        MeasureSpec.EXACTLY,
      ),
    )
  }

  override fun onSizeChanged(
    w: Int,
    h: Int,
    oldw: Int,
    oldh: Int,
  ) {
    super.onSizeChanged(w, h, oldw, oldh)
    mCenterX = width / 2
    mCenterY = height / 2
    setProgressColourAsGradient()
    mRectF.set(
      VIEW_MARGIN * 1f,
      VIEW_MARGIN,
      measuredWidth - VIEW_MARGIN,
      measuredHeight - VIEW_MARGIN,
    )
  }

  // 中心文本 bound
  private val textBound = Rect()

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    // 背景弧
    drawBack(canvas)

    // 绘制中心文本
    val valueText = (progress * lv).toInt().toString()
    textPaint.getTextBounds(valueText, 0, valueText.length, textBound)
    canvas.drawText(
      valueText,
      mRectF.centerX() - textBound.width() / 2f - 4,
      mRectF.centerY() + textBound.height() / 2f,
      textPaint,
    )
    val tipText = category

    secondTextPaint.getTextBounds(tipText, 0, tipText.length, textBound)

//    canvas.drawRoundRect(
//      mRectF.centerX() - textBound.width() / 2f - 12.dp,
//      mRectF.bottom - textBound.height() - Math.abs(textBound.top) - 4.dp,
//      mRectF.centerX() + textBound.width() / 2f + 12.dp,
//      mRectF.bottom - textBound.height() + (textBound.bottom) + 4.dp,
//      10f,
//      10f,
//      secondTextBackgroundPaint,
//    )
    canvas.drawText(
      tipText,
      mRectF.centerX() - textBound.width() / 2f,
      mRectF.bottom - textBound.height(),
      secondTextPaint,
    )
  }

  // 背景颜色
  private val bgColors =
    listOf(
      ContextCompat.getColor(context, R.color.l1),
      ContextCompat.getColor(context, R.color.l2),
      ContextCompat.getColor(context, R.color.l3),
      ContextCompat.getColor(context, R.color.l4),
      ContextCompat.getColor(context, R.color.l5),
      ContextCompat.getColor(context, R.color.l6),
      ContextCompat.getColor(context, R.color.l7),
      ContextCompat.getColor(context, R.color.l8),
    )

  // 背景渐变
  private lateinit var progressShader: SweepGradient

  // 设置背景渐变
  private fun setProgressColourAsGradient() {
    val sweepGradient =
      SweepGradient(
        mCenterX * 1f,
        mCenterY * 1f,
        bgColors.toIntArray(),
        floatArrayOf(.1f, 0.2f, .3f, .4f, .5f, .6f, .7f, .8f),
      )
    val matrix = Matrix()
    matrix.setRotate(90f, width / 2f, height / 2f)
    sweepGradient.setLocalMatrix(matrix)
    progressShader = sweepGradient
  }

  // 绘制背景弧
  private fun drawBack(canvas: Canvas) {
    linePaint.strokeWidth = 20.dp
    linePaint.color = "#1B000000".toColorInt()
    linePaint.shader = null
    canvas.drawArc(
      mRectF,
      startAngle,
      swipeAngle,
      false,
      linePaint,
    )
    canvas.drawArc(
      mRectF,
      startAngle,
      swipeAngle,
      false,
      dotLinePaint,
    )
    linePaint.strokeWidth = 14.dp
    linePaint.color = Color.WHITE
    linePaint.shader = progressShader
    canvas.drawArc(
      mRectF,
      startAngle,
      swipeAngle * progress,
      false,
      linePaint,
    )
  }
}
