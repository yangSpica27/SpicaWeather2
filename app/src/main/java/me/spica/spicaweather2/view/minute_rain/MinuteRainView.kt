package me.spica.spicaweather2.view.minute_rain

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp
import kotlin.math.absoluteValue

class MinuteRainView : View {
  // 视图的高度
  private val mHeight = 120.dp

  // 雨柱的宽度
  private var lineWidth = 12.dp

  // 文本画笔
  private val textPaint =
    TextPaint().apply {
      // 文本大小
      textSize = 14.dp
      color = ContextCompat.getColor(context, R.color.text_color_white)
    }

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  // 基准线画笔
  private val baseLinePaint =
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
      strokeCap = Paint.Cap.ROUND
    }

  private val linePaint =
    // 柱状画笔
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = ContextCompat.getColor(context, R.color.text_color_white)
      strokeWidth = lineWidth
      strokeCap = Paint.Cap.ROUND
    }

  private val data =
    mutableListOf(
      .2f,
      4f,
      2f,
      .5f,
      2f,
      2f,
      .2f,
      .8f,
      12f,
    )

  // 虚线效果
  private val dotLineEffect = DashPathEffect(floatArrayOf(2.dp, 4.dp), 0f)

  // 路径
  private val path = Path()

  // 矩形
  private val rectF = RectF()

  // 文本的矩形
  private val textRect = Rect()

  // 动画
  private val anim =
    ValueAnimator.ofFloat(0f, 1f).apply {
      // 动画时长
      duration = 450L
      addUpdateListener {
        // 更新界面
        postInvalidateOnAnimation()
      }
    }

  init {
    // 获取文本的bounds
    textPaint.getTextBounds("一小时后", 0, "一小时后".length, textRect)
  }

  fun setData(list: List<Float>) {
    data.clear()
    data.addAll(list)
  }

  fun startAnim(delay: Int) {
    anim.startDelay = delay.toLong()
    anim.start()
  }

  override fun onSizeChanged(
    w: Int,
    h: Int,
    oldw: Int,
    oldh: Int,
  ) {
    super.onSizeChanged(w, h, oldw, oldh)
    lineWidth = w / 24f - 3.dp
  }

  // 右侧文本的矩形
  private val rightTextRect = Rect()

  private val rightTextPaint =
    TextPaint().apply {
      textSize = 14.dp
      // 文本对齐方式
      color = ContextCompat.getColor(context, R.color.white)
      textAlign = Paint.Align.CENTER
    }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (data.isEmpty()) return
    // 设置基准线的宽度
    baseLinePaint.strokeWidth = 2.dp
    // 设置基准线没有虚线效果
    baseLinePaint.pathEffect = null
    // 设置基准线的颜色
    baseLinePaint.color =
      ContextCompat.getColor(context, R.color.rain_line_view_baseline_color2)
    // 画基准线
    canvas.drawLine(0f, mHeight, width * 1f, mHeight, baseLinePaint)
    // 设置虚线效果
    baseLinePaint.pathEffect = dotLineEffect
    baseLinePaint.strokeWidth = 2.dp
    baseLinePaint.color =
      ContextCompat.getColor(context, R.color.rain_line_view_baseline_color2)
    // 画虚线
    canvas.drawLine(0f, mHeight / 3f, measuredWidth.toFloat(), mHeight / 3f, baseLinePaint)
    canvas.drawLine(
      0f, mHeight / 3f * 2,
      measuredWidth.toFloat(),
      mHeight / 3f * 2,
      baseLinePaint,
    )

    val minText = "小"
    rightTextPaint.getTextBounds(minText, 0, minText.length, rightTextRect)
    // 绘制右侧的小
    canvas.drawText(
      minText,
      width - paddingRight - 30.dp - 15.dp,
      mHeight - mHeight / 6f + textRect.height() / 2f,
      rightTextPaint,
    )
    val maxText = "大"
    rightTextPaint.getTextBounds(maxText, 0, maxText.length, rightTextRect)
    // 绘制右侧的大
    canvas.drawText(
      maxText,
      width - paddingRight - 30.dp - 15.dp,
      mHeight / 3f - mHeight / 6f + textRect.height() / 2f,
      rightTextPaint,
    )
    val midText = "中"
    rightTextPaint.getTextBounds(midText, 0, midText.length, rightTextRect)

    // 绘制右侧的中
    canvas.drawText(
      midText,
      width - paddingRight - 30.dp - 15.dp,
      mHeight / 3f + mHeight / 6f + textRect.height() / 2f,
      rightTextPaint,
    )

    data.forEachIndexed { index, item ->
      // 计算x轴
      val x = index * lineWidth + lineWidth / 2f + 3.dp * index + 6.dp
      // 计算基准线
      val baseline = mHeight - baseLinePaint.strokeWidth / 2f
      val y =
        // 计算y轴
        Math
          .min(
            mHeight,
            (item * 1f) * (baseline),
          ).absoluteValue * anim.animatedValue as Float

      path.reset()
      // 设置矩形
      rectF.set(
        x * 1f - lineWidth / 2f,
        baseline - y,
        x * 1f + lineWidth / 2f,
        baseline,
      )
      if (rectF.right < width - paddingRight - 60.dp) {
        // 添加圆角矩形
        path.addRoundRect(
          rectF,
          floatArrayOf(
            lineWidth / 4f,
            lineWidth / 4f, // 左上角的圆角半径
            lineWidth / 4f,
            lineWidth / 4f, // 右上角的圆角半径
            0f,
            0f, // 右下角的圆角半径
            0f,
            0f,
          ), // 左下角的圆角半径
          Path.Direction.CW,
        )
        // 设置透明度
        linePaint.alpha = (255 * (y / mHeight)).toInt()
        canvas.drawPath(path, linePaint)
      }
    }

    canvas.drawText(
      // 绘制左侧的1小时后的文本
      "1小时后",
      5 * lineWidth + lineWidth / 2f + 3.dp * 5 + 6.dp - textRect.width() / 2f + 6.dp,
      mHeight + textRect.height() + 6.dp,
      textPaint,
    )
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    setMeasuredDimension(
      // 设置宽
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(
        (mHeight * 1f.toInt()).toInt() + textRect.height() + 12.dp.toInt(),
        heightMeasureSpec,
      ),
    )
  }
}
