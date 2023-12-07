package me.spica.spicaweather2.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp

// 空气质量指数view
private val VIEW_MARGIN = 14.dp

class AirCircleProgressView : View {

    private var mCenterX = 0
    private var mCenterY = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val mRectF: RectF = RectF()

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50.dp
        color = ContextCompat.getColor(context, R.color.textColorPrimary)
    }

    private val secondTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16.dp
        color = ContextCompat.getColor(context, R.color.textColorPrimary)
    }

    private val startColor = ContextCompat.getColor(context, R.color.line_default)

    private val endColor = ContextCompat.getColor(context, R.color.l8)

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 12.dp
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 6.dp
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.line_default)
        strokeCap = Paint.Cap.ROUND
    }

    private val startAngle = 135f

    private val swipeAngle = 270f

    private var lv = 100

    private val maxLv = 400

    private var progress = 0f

    private var category = "良"

    fun bindProgress(lv: Int, category: String) {
        this.lv = lv
        this.category = category
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun startAnim() {
        val animator = ValueAnimator.ofFloat(0f, lv * 1f / maxLv)
        animator.duration = 1500
        animator.addUpdateListener {
            progress = it.animatedValue as Float
            progress = Math.min(progress, 1f)
            ViewCompat.postInvalidateOnAnimation(this)
        }
        animator.doOnEnd {
            it.removeAllListeners()
        }
        animator.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = (MeasureSpec.getSize(widthMeasureSpec) - 2 * VIEW_MARGIN)
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(
                (width + 2 * VIEW_MARGIN).toInt(),
                MeasureSpec.EXACTLY
            ),

            MeasureSpec.makeMeasureSpec(
                (width + 2 * VIEW_MARGIN).toInt(),
                MeasureSpec.EXACTLY
            )
        )
        mRectF.set(
            VIEW_MARGIN * 1f, VIEW_MARGIN,
            measuredWidth - VIEW_MARGIN,
            measuredHeight - VIEW_MARGIN
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = width / 2
        mCenterY = height / 2
    }

    private val indicatorBitmap by lazy {
        ContextCompat.getDrawable(context, R.drawable.icon_triangle)!!.toBitmap(
            width = 12.dp.toInt(),
            height = 12.dp.toInt()
        )
    }

    private val textBound = Rect()

    private val matrix = Matrix()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 背景弧
        drawBack(canvas)

        // 绘制中心文本
        val valueText = (progress * lv).toInt().toString()
        textPaint.getTextBounds(valueText, 0, valueText.length, textBound)
        canvas.drawText(
            valueText,
            mRectF.centerX() - textBound.width() / 2f,
            mRectF.centerY() + textBound.height() / 2f,
            textPaint
        )
        val tipText = category
        secondTextPaint.getTextBounds(tipText, 0, tipText.length, textBound)
        canvas.drawText(
            tipText,
            mRectF.centerX() - textBound.width() / 2f,
            mRectF.bottom - textBound.height(),
            secondTextPaint
        )

        canvas.translate(width / 2f, height / 2f)
        canvas.rotate(-startAngle + swipeAngle * progress)
        canvas.translate(0f, -mRectF.width() / 2f - 4.dp)
        canvas.drawBitmap(indicatorBitmap, matrix, linePaint)
    }

    private val bgColors = listOf(
        ContextCompat.getColor(context, R.color.l1),
        ContextCompat.getColor(context, R.color.l2),
        ContextCompat.getColor(context, R.color.l3),
        ContextCompat.getColor(context, R.color.l4),
        ContextCompat.getColor(context, R.color.l5),
    )

    private fun drawBack(canvas: Canvas) {
        for (index in 0..4) {
            bgPaint.color = bgColors[index]
            canvas.drawArc(
                mRectF,
                startAngle + swipeAngle * 1f / 5 * (index),
                swipeAngle / 5,
                false,
                bgPaint
            )
        }
    }

    private var steadyAnim: ValueAnimator = ValueAnimator()
}
