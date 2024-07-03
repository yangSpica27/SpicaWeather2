package me.spica.spicaweather2.view.dailyItem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getIconRes
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.DailyWeatherBean
import me.spica.spicaweather2.tools.dp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 每日天气itemView
 */
class DailyItemView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    private var dailyWeatherBean: DailyWeatherBean? = null

    private var isFirst = false

    private var maxMaxTemp = 0

    private var minMinTemp = 0

    var currentTemp: Int? = null

    private val textPaint = Paint().apply {
        textSize = 12.dp
        textAlign = Paint.Align.LEFT
    }

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, 500, false))
        } else {
            textPaint.setTypeface(Typeface.DEFAULT)
        }
        setOnClickListener {
            isExpend = !isExpend
        }
    }

    private var mHight = 50.dp

    fun setData(
        dailyWeatherBean: DailyWeatherBean, isFirst: Boolean = false, maxTemp: Int, minTemp: Int
    ) {
        this.dailyWeatherBean = dailyWeatherBean
        this.isFirst = isFirst
        this.maxMaxTemp = maxTemp
        this.minMinTemp = minTemp
        startColor = if (minMinTemp < 0) {
            context.getColor(R.color.material_blue_500)
        } else if (minMinTemp < 10) {
            context.getColor(R.color.material_blue_300)
        } else if (minMinTemp < 20) {
            context.getColor(R.color.material_blue_200)
        } else {
            context.getColor(R.color.material_yellow_200)
        }
        endColor = if (maxMaxTemp < 0) {
            context.getColor(R.color.material_blue_100)
        } else if (maxMaxTemp < 10) {
            context.getColor(R.color.material_yellow_300)
        } else if (maxMaxTemp < 20) {
            context.getColor(R.color.material_yellow_500)
        } else if (maxMaxTemp < 30) {
            context.getColor(R.color.material_yellow_600)
        } else {
            context.getColor(R.color.material_yellow_700)
        }
        progressShader = null
        invalidate()
    }


    private var startColor = Color.TRANSPARENT

    private var endColor = Color.TRANSPARENT

    private var progressShader: LinearGradient? = null

    private val progressPaint = Paint().apply {
        strokeWidth = 8.dp
        strokeCap = Paint.Cap.ROUND
    }


    private val textBound = Rect()

    // 格式化为"周几"
    private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

    init {
        setPadding(15.dp.toInt(), 4.dp.toInt(), 15.dp.toInt(), 4.dp.toInt())
    }

    private val iconPaint = Paint()

    // 是否折叠模式
    private var isExpend = true
        set(value) {
            field = value
            if (isExpend) {
                expendAnim.setFloatValues(mHight, 50.dp)
            } else {
                expendAnim.setFloatValues(mHight, 135.dp)
            }
            expendAnim.start()
        }


    private val expendAnim = ValueAnimator().apply {
        this.addUpdateListener {
            mHight = it.animatedValue as Float
            requestLayout()
        }
    }

    private var isFirstDraw = true


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dailyWeatherBean?.let { dailyWeatherBean ->

            textPaint.isFakeBoldText = true
            val dateText = if (isFirst) {
                "今天"
            } else {
                sdfWeek.format(dailyWeatherBean.fxTime())
            }
            textPaint.color = if (isFirst) {
                WeatherCodeUtils.getWeatherCode(dailyWeatherBean.iconId).getThemeColor()
            } else {
                ContextCompat.getColor(context, R.color.textColorPrimary)
            }

            textPaint.textSize = 16.dp
            textPaint.getTextBounds(dateText, 0, dateText.length, textBound)
            canvas.drawText(
                dateText, paddingStart * 1f, 45.dp / 2f + textBound.height() / 2f, textPaint
            )

            canvas.save()

            val drawProgressIconBitmap =
                getOrCreateBitmap(R.drawable.ic_down, width = 20.dp.toInt(), height = 20.dp.toInt())
            canvas.translate(
                width - paddingRight - drawProgressIconBitmap.width * 1f,
                45.dp / 2f - drawProgressIconBitmap.height / 2
            )
            if (isFirstDraw) {
                canvas.rotate(
                    0f,
                    drawProgressIconBitmap.width / 2f,
                    drawProgressIconBitmap.height / 2f
                )
                isFirstDraw = false
            } else
                if (!isExpend) {
                    canvas.rotate(
                        90f * expendAnim.animatedFraction,
                        drawProgressIconBitmap.width / 2f,
                        drawProgressIconBitmap.height / 2f
                    )
                } else {
                    canvas.rotate(
                        90 - 90f * expendAnim.animatedFraction,
                        drawProgressIconBitmap.width / 2f,
                        drawProgressIconBitmap.height / 2f
                    )
                }
            canvas.drawBitmap(
                drawProgressIconBitmap,
                0f,
                0f,
                iconPaint
            )

            canvas.restore()

            textPaint.color = ContextCompat.getColor(context, R.color.textColorPrimary)
            textPaint.textSize = 16.dp
            val maxTempText = "${dailyWeatherBean.maxTemp}℃"
            textPaint.getTextBounds(maxTempText, 0, maxTempText.length, textBound)
            canvas.drawText(
                maxTempText,
                width - paddingRight - drawProgressIconBitmap.width * 1f - 30.dp - 8.dp,
                45.dp / 2f + textBound.height() / 2f,
                textPaint
            )



            textPaint.color = ContextCompat.getColor(context, R.color.textColorPrimary)
            textPaint.textSize = 16.dp
            val minTempText = "${dailyWeatherBean.minTemp}℃"
            textPaint.getTextBounds(minTempText, 0, minTempText.length, textBound)
            canvas.drawText(
                minTempText,
                width - paddingRight - drawProgressIconBitmap.width * 1f - 8.dp - 100.dp - 30.dp * 2,
                45.dp / 2f + textBound.height() / 2f,
                textPaint
            )

            // 绘制天气图标
            val bitmap = getOrCreateBitmap(
                WeatherCodeUtils.getWeatherCode(dailyWeatherBean.iconId).getIconRes()
            )

            canvas.drawBitmap(
                bitmap,
                (width - paddingRight - drawProgressIconBitmap.width * 1f - 8.dp - 100.dp - 30.dp * 2) / 2f
                        - bitmap.width / 2f
                        + paddingLeft / 2f + bitmap.width / 2f,
                45.dp / 2f - bitmap.height / 2,
                iconPaint
            )

            val left =
                width - paddingRight - drawProgressIconBitmap.width * 1f - 8.dp - 100.dp - 30.dp + 8.dp + 12.dp
            val right =
                width - paddingRight - drawProgressIconBitmap.width * 1f - 30.dp - 8.dp - 12.dp

            progressPaint.shader = null
            progressPaint.strokeWidth = 8.dp
            progressPaint.color = ContextCompat.getColor(context, R.color.rainRectColor)

            canvas.drawLine(
                left, 45.dp / 2f, right, 45.dp / 2f, progressPaint
            )

            if (progressShader == null) {
                progressShader = LinearGradient(
                    left,
                    0F,
                    right,
                    0f,
                    startColor,
                    endColor,
                    Shader.TileMode.CLAMP
                )
            }


            progressPaint.color = ContextCompat.getColor(context, R.color.black)
            progressPaint.shader = progressShader

            val left2 =
                (dailyWeatherBean.minTemp - minMinTemp) * 1f / (maxMaxTemp - minMinTemp) * (right - left) + left
            val right2 =
                (dailyWeatherBean.maxTemp - minMinTemp) * 1f / (maxMaxTemp - minMinTemp) * (right - left) + left

            canvas.drawLine(
                left2, 45.dp / 2f, right2, 45.dp / 2f, progressPaint
            )

            if (currentTemp != null) {
                progressPaint.shader = null
                val currentX =
                    (currentTemp!! - minMinTemp) * 1f / (maxMaxTemp - minMinTemp) * (right - left) + left
                progressPaint.color = Color.WHITE
                canvas.drawCircle(currentX, 45.dp / 2f, 8.dp, progressPaint)
                progressPaint.shader = progressShader
                canvas.drawCircle(currentX, 45.dp / 2f, 6.dp, progressPaint)
            }
        }
        if (!isExpend || (expendAnim.isRunning)) {
            // 绘制下半部分
            canvas.drawRoundRect(
                0f + paddingLeft,
                45.dp + 12.dp,
                width.toFloat() - paddingRight,
                mHight,
                12f,
                12f,
                rectPaint
            )
            canvas.drawText("开发中..", paddingLeft + 12.dp, 45.dp + 50.dp, textPaint)
        }
    }

    private val rectPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.rainRectColor)
    }

    companion object {
        private val bitmapPool = LruCache<Int, Bitmap>(4 * 1024 * 1024)
    }

    private fun getOrCreateBitmap(
        iconRes: Int,
        width: Int = 25.dp.toInt(),
        height: Int = 25.dp.toInt()
    ): Bitmap {
        if (bitmapPool[iconRes] != null) {
            return bitmapPool[iconRes]
        }
        val bitmap = ContextCompat.getDrawable(
            context, iconRes
        )!!.toBitmap(
            width = width, height = height, config = Bitmap.Config.ARGB_8888
        )
        bitmapPool.put(iconRes, bitmap)
        return bitmap
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            mHight.toInt()
        )
    }
}
