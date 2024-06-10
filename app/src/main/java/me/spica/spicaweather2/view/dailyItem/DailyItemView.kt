package me.spica.spicaweather2.view.dailyItem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
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

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

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

    private val textPaint = Paint().apply {
        textSize = 12.dp
        textAlign = Paint.Align.LEFT
    }

    private val textBound = Rect()

    // 格式化为"周几"
    private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

    init {
        setPadding(15.dp.toInt(), 4.dp.toInt(), 15.dp.toInt(), 4.dp.toInt())
    }

    private val iconPaint = Paint()


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dailyWeatherBean?.let { dailyWeatherBean ->
            textPaint.setTypeface(Typeface.DEFAULT)
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
                dateText, paddingStart * 1f, height / 2f + textBound.height() / 2f, textPaint
            )
            var endX = 50.dp

            val weatherNameText = dailyWeatherBean.weatherNameDay
            textPaint.getTextBounds(weatherNameText, 0, weatherNameText.length, textBound)
            canvas.drawText(
                weatherNameText, endX + 8.dp, height / 2f + textBound.height() / 2f, textPaint
            )
            endX += 60.dp

            textPaint.textSize = 16.dp
            textPaint.setTypeface(Typeface.DEFAULT)
            val maxTempText = "${dailyWeatherBean.maxTemp}℃"
            textPaint.getTextBounds(maxTempText, 0, maxTempText.length, textBound)
            canvas.drawText(
                maxTempText,
                (width - paddingRight).toFloat() - textBound.width(),
                height / 2f + textBound.height() / 2f,
                textPaint
            )

            var startX = width - paddingRight - 40.dp

            progressPaint.shader = null
            progressPaint.strokeWidth = 8.dp
            progressPaint.color = ContextCompat.getColor(context, R.color.rainRectColor)

            canvas.drawLine(
                startX * 1f - 8.dp, height / 2f, startX - width / 4f, height / 2f, progressPaint
            )

            if (progressShader == null) {
                progressShader = LinearGradient(
                    startX - width / 4f,
                    0F,
                    startX * 1f - 8.dp,
                    0f,
                    startColor,
                    endColor,
                    Shader.TileMode.CLAMP
                )
            }

            progressPaint.color = ContextCompat.getColor(context, R.color.black)
            progressPaint.shader = progressShader

            val left =
                ((startX * 1f - 8.dp) - (startX - width / 4f)) * (dailyWeatherBean.minTemp - minMinTemp) / (maxMaxTemp - minMinTemp) + startX - width / 4f

            val right =
                ((startX * 1f - 8.dp) - (startX - width / 4f)) * (dailyWeatherBean.maxTemp - minMinTemp) / (maxMaxTemp - minMinTemp) + startX - width / 4f

            canvas.drawLine(
                left, height / 2f, right, height / 2f, progressPaint
            )

            progressPaint.shader = null
            if (currentTemp != null) {
                val currentX =
                    (right - left) * (currentTemp!! - minMinTemp) / (maxMaxTemp - minMinTemp) + left
                progressPaint.color = Color.WHITE
                canvas.drawCircle(currentX, height / 2f, 10.dp, progressPaint);
                progressPaint.color = endColor
                canvas.drawCircle(currentX, height / 2f, 6.dp, progressPaint);
            }



            startX -= width / 4f + 8.dp

            val minTempText = "${dailyWeatherBean.minTemp}℃"
            textPaint.getTextBounds(minTempText, 0, minTempText.length, textBound)
            canvas.drawText(
                minTempText,
                startX - 8.dp - textBound.width(),
                height / 2f + textBound.height() / 2f,
                textPaint
            )
            startX -= 30.dp + 8.dp

            val bitmap = getOrCreateBitmap(WeatherCodeUtils.getWeatherCode(dailyWeatherBean.iconId).getIconRes())

            canvas.drawBitmap(
                bitmap, startX / 2f + endX / 2f - 16.dp, height / 2f - bitmap.height / 2, iconPaint
            )
        }
    }

    companion object {
        private val bitmapPool = LruCache<Int, Bitmap>(4 * 1024 * 1024)
    }

    private fun getOrCreateBitmap(iconRes: Int): Bitmap {
        if (bitmapPool[iconRes] != null) {
            return bitmapPool[iconRes]
        }
        val bitmap = ContextCompat.getDrawable(
            context, iconRes
        )!!.toBitmap(
            width = 28.dp.toInt(), height = 28.dp.toInt(), config = Bitmap.Config.ARGB_8888
        )
        bitmapPool.put(iconRes, bitmap)
        return bitmap
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(45.dp.toInt(), heightMeasureSpec)
        )
    }
}
