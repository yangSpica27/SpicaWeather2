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
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.blendColors
import me.spica.spicaweather2.tools.getColorWithAlpha
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.lang.StringBuilder
import java.util.concurrent.atomic.AtomicBoolean


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
            it.updateMargins(left = 15.dp)
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
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        headerText.autoMeasure()
        welcomeText.autoMeasure()
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        headerText.layout(
            paddingLeft,
            paddingTop+120.dp,
        )
        welcomeText.layout(
            paddingLeft+welcomeText.marginLeft,
            headerText.bottom,
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.TODAY_DESC.code

    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

    override fun bindData(weather: Weather) {

        headerText.text = SpannableStringBuilder()
            .append(
                "${weather.hourlyWeather[0].temp}",
                AbsoluteSizeSpan(170.dp),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            .append(
                "℃", AbsoluteSizeSpan(30.dp),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
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
        headerText.post {
            val mLinearGradient = LinearGradient(
                0f, 0f, 0f,
                headerText.height * 1f,
                intArrayOf(
                    Color.WHITE,
                    getColorWithAlpha(0.9f, Color.WHITE),
                    getColorWithAlpha(0.5f, Color.WHITE),
                ),
                floatArrayOf(
                    0f, 0.5f, 1f
                ),
                Shader.TileMode.CLAMP
            )
            headerText.paint.setShader(mLinearGradient)
            headerText.invalidate()
        }
    }
}
