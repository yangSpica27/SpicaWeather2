package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.dailyItem.DailyItemView
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

class DailyWeatherLayout(context: Context) : AViewGroup(context), SpicaWeatherCard {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var allHeight = 0f
        children.forEach { item ->
            measureChildren(widthMeasureSpec, heightMeasureSpec)
            allHeight += item.measuredHeight
        }
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(allHeight.toInt() + paddingTop + paddingBottom, heightMeasureSpec)
        )
    }

    init {
        setBackgroundResource(R.drawable.bg_card)
        
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 15.dp
            rightMargin = 15.dp
        }
        setPadding(12.dp, 8.dp, 12.dp, 8.dp)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var lastBottom = 0
        children.forEach {
            it.layout(paddingLeft, lastBottom + it.paddingTop)
            lastBottom += it.height
        }
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = HomeCardType.DAY_WEATHER.code
    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)
    override fun bindData(weather: Weather) {
        removeAllViews()
        if (weather.dailyWeather.isEmpty()) return
        var minMinTemp = weather.dailyWeather[0].minTemp
        var maxMaxTemp = weather.dailyWeather[0].maxTemp
        weather.dailyWeather.forEach {
            minMinTemp = minOf(minMinTemp, it.minTemp)
            maxMaxTemp = maxOf(maxMaxTemp, it.maxTemp)
        }
        weather.dailyWeather.forEachIndexed { index, it ->
            val lp = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val dailyItemView = DailyItemView(context)
            dailyItemView.layoutParams = lp
            addView(dailyItemView)
            if (index == 0) {
                dailyItemView.currentTemp = weather.todayWeather.temp
            }
            dailyItemView.setData(it, index == 0, maxMaxTemp, minMinTemp)
        }
    }

    override fun startEnterAnim() {
        super.startEnterAnim()
    }

    override fun resetAnim() {
        super.resetAnim()
    }
}
