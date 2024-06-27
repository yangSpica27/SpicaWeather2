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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp


// 描述卡片下面的条
class DescProgressLineView : View {


    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?) : super(context)


    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.pathBgColor)
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
        strokeWidth = 6.dp
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


    fun setProgress(progress: Float, mode: Int) {
        this.progress = progress
        this.mode = mode
        invalidate()
    }


    init {
        setPadding(12.dp.toInt(), 12.dp.toInt(), 12.dp.toInt(), 12.dp.toInt())
        ViewCompat.setElevation(this, 2.dp)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw) {
            waterShaderPaint.shader = LinearGradient(
                0f, 0f, w.toFloat(), 0f, intArrayOf(
                    Color.parseColor("#FFB3E5FC"),
                    Color.parseColor("#FF81D4FA"),
                    Color.parseColor("#FF29B6F6"),
                    Color.parseColor("#FF42A5F5"),
                ), floatArrayOf(
                    0f,
                    0.5f,
                    0.7f,
                    1f
                ), Shader.TileMode.CLAMP
            )
            uvShaderPaint.shader = LinearGradient(
                0f, 0f, w.toFloat(), 0f, intArrayOf(
                    Color.parseColor("#FF66BB6A"),
                    Color.parseColor("#FFFFEB3B"),
                    Color.parseColor("#FFFFAB40"),
                    Color.parseColor("#FFEF5350"),
                    Color.parseColor("#FFAB47BC")
                ), floatArrayOf(
                    0.15f,
                    0.3f,
                    0.6f,
                    0.85f,
                    1f
                ), Shader.TileMode.CLAMP
            )
        }
    }

    private val feelTempColors = intArrayOf(
        Color.parseColor("#FF42A5F5"),
        Color.parseColor("#FF66BB6A"),
        Color.parseColor("#FFFF9100")
    )


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            resolveSize(
                12.dp.toInt(), heightMeasureSpec
            )
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
                uvShaderPaint
            )

            uvPointPaint.color = Color.WHITE
            uvPointPaint.strokeWidth = 16.dp
            canvas.drawPoint(
                (width.toFloat() - paddingRight) * progress,
                height - uvShaderPaint.strokeWidth - paddingBottom,
                uvPointPaint
            )

            uvPointPaint.color = if (progress < 0.15f) Color.parseColor("#FF66BB6A") else
                if (progress < 0.3f) Color.parseColor("#FFFFEB3B") else
                    if (progress < 0.6f) Color.parseColor("#FFFFAB40") else
                        if (progress < 0.85f) Color.parseColor("#FFEF5350") else
                            Color.parseColor("FFAB47BC")
            uvPointPaint.strokeWidth = 12.dp
            canvas.drawPoint(
                (width.toFloat() - paddingRight) * progress,
                height - uvShaderPaint.strokeWidth - paddingBottom,
                uvPointPaint
            )
        } else
            if (mode == 2) {
                canvas.drawLine(
                    paddingLeft * 1f,
                    height - waterShaderPaint.strokeWidth - paddingBottom,
                    (width.toFloat() - paddingRight),
                    height - waterShaderPaint.strokeWidth - paddingBottom,
                    bgPaint
                )
                canvas.drawLine(
                    paddingLeft * 1f,
                    height - waterShaderPaint.strokeWidth - paddingBottom,
                    (width.toFloat() - paddingRight) * progress,
                    height - waterShaderPaint.strokeWidth - paddingBottom,
                    waterShaderPaint
                )
            } else
                if (mode == 3) {

                    feelTempPaint.color = feelTempColors[0]
                    canvas.drawLine(
                        paddingLeft * 1f,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        paddingLeft + (width.toFloat() - paddingLeft - paddingRight) * 0.45f - 6.dp,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        feelTempPaint
                    )
                    feelTempPaint.color = feelTempColors[1]
                    canvas.drawLine(
                        paddingLeft + (width.toFloat() - paddingLeft - paddingRight) * 0.45f + 6.dp,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        paddingLeft + (width.toFloat() - paddingRight - paddingLeft) * 0.625f - 6.dp,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        feelTempPaint
                    )

                    feelTempPaint.color = feelTempColors[2]
                    canvas.drawLine(
                        paddingLeft + (width.toFloat() - paddingRight - paddingLeft) * 0.625f + 6.dp,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        paddingLeft + (width.toFloat() - paddingRight - paddingLeft) * 1f,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        feelTempPaint
                    )

                    feelTempPaint2.color = Color.BLACK
                    feelTempPaint2.strokeWidth = 16.dp

                    canvas.drawPoint(
                        (width.toFloat() - paddingRight) * progress,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        feelTempPaint2
                    )
                    feelTempPaint2.color = if (progress < 0.45) {
                        feelTempColors[0]
                    } else if (progress < 0.625) {
                        feelTempColors[1]
                    } else {
                        feelTempColors[2]
                    }

                    canvas.drawPoint(
                        (width.toFloat() - paddingRight) * progress,
                        height - feelTempPaint.strokeWidth - paddingBottom,
                        feelTempPaint2
                    )


                } else if (mode == 4) {
                    sunrisePaint.strokeWidth = 6.dp
                    sunrisePaint.color = Color.parseColor("#FFE5E5E5")

                    canvas.drawArc(
                        paddingLeft * 1f, paddingTop * 1f,
                        width.toFloat() - paddingRight,
                        height.toFloat() - sunrisePaint.strokeWidth,
                        270f - 135 / 2f, 135f,
                        false, sunrisePaint
                    )
                    sunrisePaint.color = Color.parseColor("#FF999999")
                    // 获取对应progress的角度
                    val progressAngle = 135 * progress

                    path.reset()
                    path.addArc(
                        paddingLeft * 1f, paddingTop * 1f,
                        width.toFloat() - paddingRight,
                        height.toFloat() - sunrisePaint.strokeWidth,
                        270f - 135 / 2f, progressAngle,
                    )
                    canvas.drawPath(path, sunrisePaint)

                    pathMeasure.setPath(path, false)

                    // 绘制日出日落的点
                    pathMeasure.getPosTan(
                        pathMeasure.length,
                        pos,
                        floatArrayOf(0f, 0f)
                    )
                    sunrisePaint.strokeWidth = 25.dp
                    sunrisePaint.color = Color.parseColor("#FFD9D9D9")
                    canvas.drawPoint(pos[0], pos[1], sunrisePaint)
                    sunrisePaint.strokeWidth = 18.dp
                    sunrisePaint.color = Color.parseColor("#FF555555")
                    canvas.drawPoint(pos[0], pos[1], sunrisePaint)

                }
    }



}
