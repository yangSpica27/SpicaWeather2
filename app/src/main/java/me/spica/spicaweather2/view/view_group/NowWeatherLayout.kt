package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class NowWeatherLayout(context: Context) : AViewGroup(context = context), SpicaWeatherCard {


    private val weatherNameText = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelLarge)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 8.dp,
            )
        }
        includeFontPadding = false
        lastBaselineToBottomHeight = 0
        firstBaselineToTopHeight = 0
    }

    private val currentTempText = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_DisplayLarge)
        textSize = 40.dp.toFloat()
//        setBackgroundColor(Color.BLACK)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 14.dp,
                top = 80.dp
            )
        }
        includeFontPadding = false
        lastBaselineToBottomHeight = 0
        firstBaselineToTopHeight = 0
    }

    private val currentTempTagText = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_DisplayMedium)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 8.dp
            )
        }
        includeFontPadding = false
        lastBaselineToBottomHeight = 0
        firstBaselineToTopHeight = 0
    }

    init {
        isFocusable = false
        isClickable = false
        setTextViewStyles(weatherNameText)
        setTextViewStyles(currentTempText)
        setTextViewStyles(currentTempTagText)
        addView(weatherNameText)
        addView(currentTempText)
        addView(currentTempTagText)
    }

    private fun setTextViewStyles(textView: TextView) {
        val mLinearGradient = LinearGradient(
            0f,
            0f, 0f, textView.height * 1f,
            Color.WHITE,
            Color.parseColor("#CCFFFFFF"),
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = mLinearGradient
        textView.postInvalidateOnAnimation()
    }


    private val screenHeight by lazy {
        context.resources.displayMetrics.heightPixels
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        weatherNameText.autoMeasure()
        currentTempText.autoMeasure()
        currentTempTagText.autoMeasure()
        setMeasuredDimension(
            measuredWidth,
            screenHeight / 5*3
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        currentTempText.layout(
            currentTempText.marginLeft,
            currentTempText.marginTop
        )
        currentTempTagText.layout(
            currentTempText.right + currentTempTagText.marginLeft,
            currentTempText.top +  currentTempTagText.height -20.dp
        )

        weatherNameText.layout(
            currentTempText.right + weatherNameText.marginLeft,
            currentTempText.bottom -
                weatherNameText.height -
                currentTempText.lastBaselineToBottomHeight
        )

    }


    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = 0

    override var hasInScreen: Boolean = false

    override fun bindData(weather: Weather) {
        currentTempText.text = weather.todayWeather.temp.toString()
        currentTempTagText.text = "℃"
        weatherNameText.text = "${weather.todayWeather.weatherName} 体感${weather.todayWeather.feelTemp}℃"
    }


}