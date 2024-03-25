package me.spica.spicaweather2.view.view_group

import android.R.attr.text
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Gravity
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
import java.lang.StringBuilder


class TodayDescLayout(context: Context) : AViewGroup(context = context), SpicaWeatherCard {

    @SuppressLint("SetTextI18n")
    private val headerText = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_HeadlineLarge)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text = "--"
        setTextColor(Color.WHITE)
        includeFontPadding = false
    }

    private val welcomeText = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelLarge)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.updateMargins(left = 14.dp)
        }
        text = "--"
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21f)
        includeFontPadding = false
    }


    init {
        isFocusable = false
        isClickable = false
        addView(headerText)
        addView(welcomeText)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        headerText.autoMeasure()
        welcomeText.autoMeasure()
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        welcomeText.layout(
            welcomeText.marginLeft,
            measuredHeight - welcomeText.measuredHeight,
        )
        headerText.layout(
            headerText.marginLeft,
            -welcomeText.marginTop + welcomeText.top - headerText.measuredHeight,
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.NOW_WEATHER.code

    override var hasInScreen: Boolean = false

    override fun bindData(weather: Weather) {

        headerText.text = SpannableStringBuilder()
            .append(
                "${weather.hourlyWeather[0].temp}",
                AbsoluteSizeSpan(100.dp),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            .append(
                "℃", AbsoluteSizeSpan(22.dp),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        val mLinearGradient = LinearGradient(
            0f, 0f, 0f,
            headerText.height * 1.5F,
            Color.WHITE,
            Color.parseColor("#80ffffff"),
            Shader.TileMode.CLAMP
        )
        headerText.paint.setShader(mLinearGradient)
        headerText.invalidate()
        val bottomText = StringBuilder()
        bottomText.append(weather.todayWeather.weatherName)
        bottomText.append("，")
        bottomText.append("降水概率")
        bottomText.append(weather.hourlyWeather[0].pop)
        bottomText.append("%")
//        if (weather.alerts.isNotEmpty()) {
//            bottomText.append("\n")
//            bottomText.append(weather.alerts[0].title)
//        }
        welcomeText.text = bottomText

    }
}
