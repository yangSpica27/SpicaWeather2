package me.spica.spicaweather2.view;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.tools.getColorWithAlpha

/**
 * @ClassName NowWeatherInfoCard2
 * @Author Spica2 7
 * @Date 2024/1/15 16:32
 */
class NowWeatherInfoCard2(context: Context) : View(context) {



    init {
        setBackgroundResource(R.drawable.bg_card)
    }


    private var leftTopBitmap: Bitmap? = null

    private var weatherTypeBitmap: Bitmap? = null

    private var tempText = "0℃"

    private var weatherTypeText = "晴"

    private var windIconBitmap: Bitmap? = null

    private var windText = "东北风 1级"

    private var humidity: Bitmap? = null

    private var humidityText = "0%"

    private var airQualityText = "24"

    private var airCity = "南京AQI"

    private var airQualityDesc = "优"

    private var pmText = "24毫克/立方米"

    private val cardHeight = 300.dp.toInt()

    private val clipPath = Path()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clipPath.reset()
        clipPath.addRoundRect(
            0f,
            0f,
            w.toFloat(),
            h.toFloat(),
            20.dp,
            20.dp,
            Path.Direction.CW
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            measuredWidth,
            cardHeight
        )
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = getColorWithAlpha(0.8f, context.getColor(R.color.l2))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.clipPath(clipPath)
        canvas.drawColor(context.getColor(R.color.white))
        canvas.drawRect(
            0f,
            0f,
            width * 1f,
            height * .7f,
            backgroundPaint
        )
    }


    fun bindData(weather: Weather) {
        pmText = weather.air.pm10.toString() + "毫克/立方米"
        airQualityText = weather.air.aqi.toString()
        airQualityDesc = weather.air.primary
        airCity = weather.cityName
        humidityText = weather.todayWeather.water.toString() + "%"
        windText = "${weather.todayWeather.windSpeed}m/s"
        weatherTypeText = weather.todayWeather.weatherName
        tempText = weather.todayWeather.temp.toString() + "℃"
        invalidate()
    }


}
