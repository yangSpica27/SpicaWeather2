package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class TodayDescLayout(context: Context) : AViewGroup(context = context), SpicaWeatherCard {

    @SuppressLint("SetTextI18n")
    private val tempName = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_HeadlineLarge)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.updateMargins(
                top = 20.dp, left = 14.dp
            )
        }
        text = "--"
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 44f)
    }

    private val weatherName = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelLarge)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.updateMargins(
                top = 12.dp, left = 14.dp
            )
        }
        text = "--"
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
    }


    init {
        isFocusable = false
        isClickable = false
        addView(tempName)
        addView(weatherName)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 100.dp
        }
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        tempName.autoMeasure()
        weatherName.autoMeasure()
        setMeasuredDimension(
            measuredWidth,
            tempName.measuredHeightWithMargins + weatherName.measuredHeightWithMargins
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        tempName.layout(
            tempName.marginLeft, tempName.marginTop
        )
        weatherName.layout(
            weatherName.marginLeft, tempName.bottom + weatherName.marginTop
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.NOW_WEATHER.code

    override var hasInScreen: Boolean = false

    override fun bindData(weather: Weather) {
        tempName.text = "${weather.hourlyWeather[0].temp}℃,${weather.todayWeather.weatherName}"
        weatherName.text = "${weather.welcomeText}"
    }
}
