package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class TodayExtraLayout(context: Context) : AViewGroup(context), SpicaWeatherCard {


    private val waterTitle = AppCompatTextView(context).apply {
        initTitleText(this)
    }

    private val waterValue = AppCompatTextView(context).apply {
        initValueText(this)
    }

    private val windSpeedTitleText = AppCompatTextView(context).apply {
        initTitleText(this)
    }

    private val windSpeedValueText = AppCompatTextView(context).apply {
        initValueText(this)
    }


    private val windPaTitleText = AppCompatTextView(context).apply {
        initTitleText(this)
    }

    private val windPaValueText = AppCompatTextView(context).apply {
        initValueText(this)
    }


    private fun initTitleText(textView: AppCompatTextView) {
        textView.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.setTextAppearance(R.style.TextAppearance_Material3_LabelLarge)
        textView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimaryLight))
    }

    private fun initValueText(textView: AppCompatTextView) {
        textView.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(top = 12.dp)
        }
        textView.setTextAppearance(R.style.TextAppearance_Material3_HeadlineSmall)
        textView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
    }

    init {
        isFocusable = false
        isClickable = false
        addView(waterValue)
        addView(waterTitle)
        addView(windPaValueText)
        addView(windPaTitleText)
        addView(windSpeedValueText)
        addView(windSpeedTitleText)
        updatePadding(14.dp, 12.dp, 14.dp, 12.dp)
        setBackgroundResource(R.drawable.bg_card)
        val lp = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
            it.updateMargins(left = 14.dp, right = 14.dp, top = 12.dp)
        }
        layoutParams = lp
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        children.forEach {
            it.autoMeasure()
        }
        setMeasuredDimension(
            measuredWidth, paddingTop + paddingBottom + waterTitle.measuredHeightWithMargins + waterValue.measuredHeightWithMargins
        )
    }


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        waterTitle.layout(
            paddingLeft, paddingTop
        )
        waterValue.layout(
            paddingLeft,
            waterTitle.bottom + waterValue.marginTop,
        )
        windPaTitleText.layout(
            windPaTitleText.toHorizontalCenter(this),
            paddingTop
        )
        windPaValueText.layout(
            windPaValueText.toHorizontalCenter(this),
            waterTitle.bottom + windPaValueText.marginTop
        )

        windSpeedTitleText.layout(
            paddingRight,
            paddingTop,
            true
        )

        windSpeedValueText.layout(
            paddingRight,
            windSpeedTitleText.bottom + windPaValueText.marginTop,
            true
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = 0

    override var hasInScreen: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {
        waterTitle.text = "湿度"
        windPaTitleText.text = "气压"
        windSpeedTitleText.text = "风速"
        waterValue.text = "${weather.todayWeather.water}%"
        windPaValueText.text = "${weather.todayWeather.windPa}hpa"
        windSpeedValueText.text = "${weather.todayWeather.windSpeed}km/h"
    }


}