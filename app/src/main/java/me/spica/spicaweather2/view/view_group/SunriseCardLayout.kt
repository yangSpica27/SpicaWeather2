package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.view.SunriseView
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class SunriseCardLayout(context: Context) : AViewGroup(context), SpicaWeatherCard {

    private val cardTypeText = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_TitleMedium)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private val subTitleText = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_TitleSmall)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private val sunriseView = SunriseView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private val sunriseText = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_BodyMedium)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
    }

    private val sunsetText = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_BodyMedium)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
    }

    init {
        setPadding(14.dp, 12.dp, 14.dp, 12.dp)
        addView(cardTypeText)
        addView(subTitleText)
        addView(sunriseView)
        addView(sunriseText)
        addView(sunsetText)

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(
                left = 14.dp,
                right = 14.dp
            )
        }
        setBackgroundResource(R.drawable.bg_card)
        ViewCompat.setElevation(this, 4.dp.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        cardTypeText.autoMeasure()
        subTitleText.autoMeasure()
        sunriseText.autoMeasure()
        sunsetText.autoMeasure()
        sunriseView.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            sunriseView.defaultHeightMeasureSpec(this)
        )
        setMeasuredDimension(
            measuredWidth,
            cardTypeText.measuredHeightWithMargins +
                    sunriseView.measuredHeightWithMargins +
                    sunriseText.measuredHeightWithMargins +
                    paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        cardTypeText.layout(
            paddingLeft, paddingTop
        )
        subTitleText.layout(
            paddingRight,
            subTitleText.toViewVerticalCenter(cardTypeText),
            true
        )
        sunriseView.layout(
            paddingLeft, cardTypeText.bottom
        )
        sunriseText.layout(
            paddingLeft, sunriseView.bottom
        )
        sunsetText.layout(
            paddingRight,
            sunriseView.bottom,
            true
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = 6

    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

    private val sdf = SimpleDateFormat("HH:mm", Locale.CHINA)


    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {


        cardTypeText.text = "日出日落"
        val startTime = weather.dailyWeather[0].sunriseDate()
        val endTime = weather.dailyWeather[0].sunsetDate()
        val subTitle = weather.dailyWeather[0].moonParse
        val themeColor = weather.getWeatherType().getThemeColor()
        subTitleText.text = subTitle
        cardTypeText.setTextColor(themeColor)
        sunriseView.bindTime(startTime, endTime)
        sunriseView.themeColor = themeColor
        sunriseText.text = "上午 ${sdf.format(startTime)}"
        sunsetText.text = "下午 ${sdf.format(endTime)}"
    }

    override fun startEnterAnim() {
        super.startEnterAnim()
        doOnMainThreadIdle({
            sunriseView.startAnim()
        })
    }

    override fun resetAnim() {
        super.resetAnim()
    }
}
