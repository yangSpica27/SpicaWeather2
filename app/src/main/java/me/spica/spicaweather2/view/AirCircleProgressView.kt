package me.spica.spicaweather2.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
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
            color = ContextCompat.getColor(context, R.color.textColorPrimary)
        }

    private val secondTextPaint =
        TextPaint().apply {
            textSize = 16.dp
            color = ContextCompat.getColor(context, R.color.white)
            typeface = Typeface.DEFAULT_BOLD
        }

    private val secondTextBackgroundPaint =
        TextPaint().apply {
            color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
            style = Paint.Style.FILL
        }

    private val linePaint =
        Paint().apply {
            strokeWidth = 12.dp
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
            strokeCap = Paint.Cap.SQUARE
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
        if (lv < 50) {
            textPaint.color = ContextCompat.getColor(context, R.color.l1)
        } else if (lv < 100) {
            textPaint.color = ContextCompat.getColor(context, R.color.l2)
        } else if (lv < 150) {
            textPaint.color = ContextCompat.getColor(context, R.color.l5)
        } else if (lv < 200) {
            textPaint.color = ContextCompat.getColor(context, R.color.l6)
        } else if (lv < 300) {
            textPaint.color = ContextCompat.getColor(context, R.color.l7)
        } else {
            textPaint.color = ContextCompat.getColor(context, R.color.l8)
        }
        secondTextBackgroundPaint.color = textPaint.color
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
            mRectF.centerX() - textBound.width() / 2f-4,
            mRectF.centerY() + textBound.height() / 2f,
            textPaint,
        )
        val tipText = category

        secondTextPaint.getTextBounds(tipText, 0, tipText.length, textBound)

        canvas.drawRoundRect(
            mRectF.centerX() - textBound.width() / 2f - 12.dp,
            mRectF.bottom - textBound.height() - Math.abs(textBound.top) - 4.dp,
            mRectF.centerX() + textBound.width() / 2f + 12.dp,
            mRectF.bottom - textBound.height() + (textBound.bottom) + 4.dp,
            10f,
            10f,
            secondTextBackgroundPaint,
        )
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
        linePaint.strokeWidth = 8.dp
        linePaint.color = Color.parseColor("#F5F5F5")
        linePaint.shader = null
        canvas.drawArc(
            mRectF,
            startAngle,
            swipeAngle,
            false,
            linePaint,
        )
        linePaint.strokeWidth = 12.dp
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
