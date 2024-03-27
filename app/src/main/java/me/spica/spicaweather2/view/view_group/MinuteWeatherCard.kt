package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.WeatherType
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.minute_rain.MinuteRainView
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class MinuteWeatherCard(
    context: Context
) : AViewGroup(context = context), SpicaWeatherCard {


    private val titleText = AppCompatTextView(context).apply {
        layoutParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(bottom = 8.dp)
        }
        setTextAppearance(R.style.TextAppearance_Material3_LabelLarge)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
        setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
        text = "分钟级降水"
        addView(this)
    }

    init {
        isFocusable = false
        isClickable = false
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(14.dp, 12.dp, 14.dp, 12.dp)
        setBackgroundResource(R.drawable.bg_card)
        ViewCompat.setElevation(this, 4.dp.toFloat())
    }

    private val rainView = MinuteRainView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(this)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        titleText.layout(paddingLeft, paddingTop)
        rainView.layout(paddingLeft, titleText.bottom + titleText.marginBottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        rainView.autoMeasure()
        titleText.autoMeasure()
        setMeasuredDimension(
            measuredWidth,
            paddingTop + paddingBottom + rainView.measuredHeightWithMargins + titleText.measuredHeightWithMargins
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.MINUTE_WEATHER.code

    override var hasInScreen: Boolean = false
    override fun bindData(weather: Weather) {
        rainView.setData(weather.minutelies.map { (it.precip.toFloatOrNull() ?: 0f)*5f })
        rainView.startAnim(600)
        visibility =
            if (WeatherCodeUtils.getWeatherCode(weather.todayWeather.iconId) == WeatherType.WEATHER_RAINY) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
    }
}