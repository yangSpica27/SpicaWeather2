package me.spica.spicaweather2.view.dailyItem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getIconRes
import me.spica.spicaweather2.persistence.entity.weather.DailyWeatherBean
import me.spica.spicaweather2.tools.dp
import java.text.SimpleDateFormat
import java.util.Locale

class DailyItemView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private var dailyWeatherBean: DailyWeatherBean? = null

    private var isFirst = false

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

   fun setData(dailyWeatherBean: DailyWeatherBean, isFirst: Boolean = false) {
        this.dailyWeatherBean = dailyWeatherBean
        invalidate()
    }


    private val expandAnim = ValueAnimator.ofFloat().setDuration(225)

    fun expand(expand: Boolean) {

    }


    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 8.dp
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 12.dp
        textAlign = Paint.Align.LEFT
    }

    private val textBound = Rect()

    // 格式化为"周几"
    private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

    init {
        setPadding(14.dp.toInt(),0, 14.dp.toInt(),0)
    }

    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dailyWeatherBean?.let { dailyWeatherBean ->
            val dateText = if (isFirst) {
                "今天"
            } else {
                sdfWeek.format(dailyWeatherBean.fxTime())
            }
            textPaint.color = ContextCompat.getColor(context, R.color.textColorPrimary)
            textPaint.textSize = 18.dp
            textPaint.getTextBounds(dateText, 0, dateText.length, textBound)
            canvas.drawText(
                dateText,
                paddingStart * 1f,
                height/2f+textBound.height()/2f,
                textPaint
            )
            var endX = 50.dp

            val weatherNameText = dailyWeatherBean.weatherNameDay
            textPaint.getTextBounds(weatherNameText, 0, weatherNameText.length, textBound)
            canvas.drawText(
                weatherNameText,
                endX+12.dp,
                height/2f+textBound.height()/2f,
                textPaint
            )
            endX += 60.dp


            textPaint.textSize = 16.dp
            val maxTempText = "${dailyWeatherBean.maxTemp}℃"
            textPaint.getTextBounds(maxTempText, 0, maxTempText.length, textBound)
            canvas.drawText(
                maxTempText,
                (width - paddingRight).toFloat() - textBound.width(),
                height/2f+textBound.height()/2f,
                textPaint
            )

            var startX = width-paddingRight - 30.dp

            canvas.drawLine(
                startX * 1f - 12.dp,
                height / 2f,
                startX - width / 4f,
                height / 2f,
                progressPaint
            )

            startX -= width / 4f + 12.dp

            val minTempText = "${dailyWeatherBean.minTemp}℃"
            textPaint.getTextBounds(minTempText, 0, minTempText.length, textBound)
            canvas.drawText(
                minTempText,
                startX - 12.dp - textBound.width(),
                height/2f+textBound.height()/2f,
                textPaint
            )
            startX -=  30.dp+12.dp

            val bitmap = ContextCompat.getDrawable(context,WeatherCodeUtils.getWeatherCode(dailyWeatherBean.iconId).getIconRes())!!.toBitmap(
                width = 24.dp.toInt(),height = 24.dp.toInt(), config = Bitmap.Config.ARGB_8888)

            canvas.drawBitmap(bitmap,startX/2f+endX/2f-12.dp,height/2f -12.dp,iconPaint)

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth,45.dp.toInt())
    }

}