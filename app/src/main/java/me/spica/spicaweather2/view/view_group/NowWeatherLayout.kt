package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.NowWeatherInfoCard
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class NowWeatherLayout(context: Context) : AViewGroup(context = context), SpicaWeatherCard {

    @SuppressLint("SetTextI18n")
    private val dateTimeTextView = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_HeadlineLarge)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                top = 20.dp, left = 14.dp
            )
        }
        paint.isFakeBoldText = true
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        paint.style = Paint.Style.FILL_AND_STROKE
        0.7.toFloat().also { paint.strokeWidth = it }
        text =
            "${DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_NO_YEAR)} ${DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_WEEKDAY)}"
        setTextColor(Color.WHITE)
    }

    private val helloText = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelLarge)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                top = 12.dp, left = 14.dp
            )
            paint.isFakeBoldText = true
        }
        text = "所赴远山，其下皆川"
        setTextColor(Color.WHITE)
    }

    val nowWeatherInfoCard = NowWeatherInfoCard(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                top = 150.dp, left = 14.dp, right = 14.dp
            )
        }
    }

    init {
        isFocusable = false
        isClickable = false
        addView(dateTimeTextView)
        addView(helloText)
        addView(nowWeatherInfoCard)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 100.dp
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        dateTimeTextView.autoMeasure()
        helloText.autoMeasure()
        nowWeatherInfoCard.measure(
            (measuredWidth - nowWeatherInfoCard.marginLeft - nowWeatherInfoCard.marginRight).toExactlyMeasureSpec(), nowWeatherInfoCard.defaultHeightMeasureSpec(this)
        )
        setMeasuredDimension(
            measuredWidth, dateTimeTextView.measuredHeightWithMargins + helloText.measuredHeightWithMargins + nowWeatherInfoCard.measuredHeightWithMargins
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        dateTimeTextView.layout(
            dateTimeTextView.marginLeft, dateTimeTextView.marginTop
        )
        helloText.layout(
            helloText.marginLeft, dateTimeTextView.bottom + helloText.marginTop
        )
        nowWeatherInfoCard.layout(
            nowWeatherInfoCard.marginLeft, helloText.bottom + nowWeatherInfoCard.marginTop
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.NOW_WEATHER.code

    override var hasInScreen: Boolean = false

    override fun bindData(weather: Weather) {
        nowWeatherInfoCard.bindData(weather)
        helloText.text = weather.welcomeText
    }
}
