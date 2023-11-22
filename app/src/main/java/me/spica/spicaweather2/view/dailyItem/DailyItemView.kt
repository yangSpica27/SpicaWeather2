package me.spica.spicaweather2.view.dailyItem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import me.spica.spicaweather2.persistence.entity.weather.DailyWeatherBean
import me.spica.spicaweather2.tools.dp
import java.text.SimpleDateFormat
import java.util.Locale

class DailyItemView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var dailyWeatherBean: DailyWeatherBean? = null

    private var isFirst = false

    init {
        setPadding(14.dp.toInt(), 12.dp.toInt(), 12.dp.toInt(), 14.dp.toInt())
    }

    private fun setData(dailyWeatherBean: DailyWeatherBean, isFirst: Boolean = false) {
        this.dailyWeatherBean = dailyWeatherBean
        invalidate()
    }


    private val expandAnim = ValueAnimator.ofFloat().setDuration(225)

    fun expand(expand: Boolean) {

    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 12.dp
        textAlign = Paint.Align.LEFT
    }

    private val textBound = Rect()

    // 格式化为"周几"
    private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dailyWeatherBean?.let { dailyWeatherBean ->
            val dateText = if (isFirst) {
                "今天"
            } else {
                sdfWeek.format(dailyWeatherBean.fxTime())
            }
            textPaint.getTextBounds(dateText, 0, dateText.length, textBound)
            canvas.drawText(
                dateText,
                paddingStart * 1f,
                paddingTop + textBound.height() / 2f,
                textPaint
            )
            var endX = textBound.width() + paddingStart * 1f

            val weatherNameText = dailyWeatherBean.weatherNameDay
            textPaint.getTextBounds(weatherNameText, 0, weatherNameText.length, textBound)
            canvas.drawText(
                weatherNameText,
                endX,
                20.dp + paddingTop + textBound.height() / 2f,
                textPaint
            )
            endX += textBound.width() + 20.dp


            var startX = width - paddingRight

            val maxTempText = "${dailyWeatherBean.maxTemp}℃"
            textPaint.getTextBounds(maxTempText, 0, maxTempText.length, textBound)
            canvas.drawText(
                maxTempText,
                (width - paddingRight).toFloat(),
                20.dp + paddingTop + textBound.height() / 2f,
                textPaint
            )
        }
    }

}