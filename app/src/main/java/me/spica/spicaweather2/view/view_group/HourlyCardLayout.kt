package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.line.HourlyLineView
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

class HourlyCardLayout(context: Context) : AViewGroup(context), SpicaWeatherCard {

    private val hourlyLineView = HourlyLineView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    init {
        setPadding(0, 12.dp, 0, 12.dp)
        setBackgroundResource(R.drawable.bg_card)
        ViewCompat.setElevation(this, 4.dp.toFloat())
        addView(hourlyLineView)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 14.dp
            rightMargin = 14.dp
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        hourlyLineView.autoMeasure()
        setMeasuredDimension(
            measuredWidth,
            paddingTop + paddingBottom +
                hourlyLineView.measuredHeightWithMargins
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        hourlyLineView.layout(
            paddingLeft,
            paddingTop
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = HomeCardType.HOUR_WEATHER.code
    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)
    override fun bindData(weather: Weather) {
        hourlyLineView.setData(weather.hourlyWeather)
    }
}
