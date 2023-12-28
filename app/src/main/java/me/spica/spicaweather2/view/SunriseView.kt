@file:Suppress("unused")

package me.spica.spicaweather2.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp
import java.util.*

// 日出view

private val mMargin = 14.dp
private const val ARC_ANGLE = 135

class SunriseView : View {

    private val drawablePaint = Paint( ).apply {
        isDither = true
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.material_yellow_600)
        maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)
    }

    private val sunSize = 28.dp

    // =========各个文本的bound========

    private val mRectF: RectF = RectF()

    // 用于清除绘制内容
    private val clearfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    var themeColor = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
        set(value) {
            field = value
            shader = LinearGradient(
                0F,
                0F,
                0F,
                height * 1f,
                getColorWithAlpha(.5f, themeColor),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            dottedLinePaint.color = getColorWithAlpha(.5f, value)
            linePaint.color = value
            postInvalidate()
        }

    // 区域绘制
    private val pathPaint = Paint( ).apply {
        style = Paint.Style.FILL
    }

    private val dottedLinePaint = Paint( ).apply {
        pathEffect = DashPathEffect(floatArrayOf(5.dp, 2.dp), 0F)
        strokeWidth = 2.dp
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.dottedLineColor)
    }

    private val linePaint = Paint( ).apply {
        strokeWidth = 4.dp
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
    }

    // 是否绘制
    private var isDraw = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var startTime = 0

    private var currentTime = 100

    private var endTime = 200

    private lateinit var shader: Shader

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = (MeasureSpec.getSize(widthMeasureSpec) - 2 * mMargin)
        val deltaRadians = Math.toRadians((180 - ARC_ANGLE) / 2.0)
        val radius = (width / 2 / Math.cos(deltaRadians)).toInt()
        val height = (radius - width / 2 * Math.tan(deltaRadians)).toInt()
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(
                (width + 2 * mMargin).toInt(),
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(
                ((height + 2 * 8.dp).toInt() + 12.dp).toInt(),
                MeasureSpec.EXACTLY
            )
        )

        val centerX = measuredWidth / 2F
        val centerY = (8.dp + radius) * 1F + 12.dp
        mRectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    /**
     *  设置时间
     */
    fun bindTime(startTime: Date, endTime: Date, currentTime: Date = Date()) {
        this.startTime = decodeTime(startTime)
        this.endTime = decodeTime(endTime)
        this.currentTime = decodeTime(currentTime)
        ensureProgress()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shader = LinearGradient(
            0F,
            0F,
            0F,
            height * 1f,
            getColorWithAlpha(.5f, themeColor),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
    }


    fun startAnim() {
        val animator = ValueAnimator.ofInt(0, currentTime - startTime)
        animator.duration = 1500
        animator.addUpdateListener {
            progress = it.animatedValue as Int
            ViewCompat.postInvalidateOnAnimation(this)
        }
        animator.start()
    }

    private var progress = 100

    private var max = 200

    private fun ensureProgress() {
        max = endTime - startTime
        progress = currentTime - startTime
        progress = Math.max(0, progress)
        progress = Math.min(max, progress)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startAngle: Float = 270f - ARC_ANGLE / 2f

        var progressSweepAngle = (progress.toFloat() / max.toFloat() * ARC_ANGLE)

        progressSweepAngle = Math.min(ARC_ANGLE * 1f, progressSweepAngle)

        progressSweepAngle = Math.max(0f, progressSweepAngle)

        val progressEndAngle = startAngle + progressSweepAngle

        val deltaAngle = progressEndAngle - 180

        val deltaWidth = Math.abs(
            mRectF.width() / 2f *
                Math.cos(Math.toRadians(deltaAngle.toDouble()))
        ).toFloat()

        val deltaHeight =
            Math.abs(mRectF.width() / 2f * Math.sin(Math.toRadians(deltaAngle.toDouble())))
                .toFloat()

        val iconPositionX =
            if (
                progressEndAngle > 270
            ) {
                mRectF.centerX() + deltaWidth
            } else {
                mRectF.centerX() - deltaWidth
            }

        val iconPositionY = mRectF.centerY() - deltaHeight
        val layerId = canvas.saveLayer(
            mRectF.left, mRectF.top, mRectF.right, mRectF.top + mRectF.height() / 2,
            null
        )

        // 绘制阴影
        pathPaint.shader = shader

        canvas.drawArc(
            mRectF,
            270 - ARC_ANGLE / 2f,
            ARC_ANGLE * 1f,
            false,
            pathPaint
        )
        pathPaint.xfermode = clearfermode
        pathPaint.shader = null
        canvas.drawRect(
            (
                (
                    mRectF.centerX() + mRectF.width() / 2 *
                        Math.cos(
                            (360 - progressEndAngle) *
                                Math.PI / 180
                        )
                    ).toFloat()
                ),
            mRectF.top,
            mRectF.right,
            mRectF.top + mRectF.height() / 2,
            pathPaint
        )

        pathPaint.xfermode = null
        canvas.restoreToCount(layerId)

        // 绘制背景虚线
        canvas.drawArc(
            mRectF,
            startAngle,
            ARC_ANGLE.toFloat(),
            false,
            dottedLinePaint
        )

        // 途径线
        canvas.drawArc(
            mRectF,
            270 - ARC_ANGLE / 2f,
            progressSweepAngle,
            false,
            linePaint
        )

        // 绘制底线
        canvas.drawLine(
            mMargin,
            measuredHeight - 8.dp,
            measuredWidth - mMargin,
            measuredHeight - 8.dp,
            dottedLinePaint
        )
        val restoreCount: Int = canvas.save()

        canvas.translate(iconPositionX, iconPositionY)
        canvas.drawCircle(0f, 0f, sunSize / 2, drawablePaint)

        canvas.restoreToCount(restoreCount)
    }

    @Suppress("DEPRECATION")
    private fun decodeTime(time: Date): Int {
        return time.hours * 60 + time.minutes
    }

    private fun getColorWithAlpha(alpha: Float, baseColor: Int): Int {
        val a = Math.min(255, Math.max(0, (alpha * 255).toInt())) shl 24
        val rgb = 0x00ffffff and baseColor
        return a + rgb
    }
}
