package me.spica.spicaweather2.view

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.view_group.AViewGroup
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

// 当前天气信息卡片
class NowWeatherInfoCard(
    context: Context,
) : AViewGroup(context = context),
    SpicaWeatherCard {
    init {
        setBackgroundResource(R.drawable.bg_card)
    }

    // 湿度
    private val waterText =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setTextColor(ContextCompat.getColor(context, R.color.humidness_color))
            updatePadding(bottom = 4.dp)
            text = "--"
        }

    private val bottomTextWater =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setTextColor(ContextCompat.getColor(context, R.color.humidness_color))
            text = "湿度"
        }

    // 风速
    private val windSpeedText =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setTextColor(ContextCompat.getColor(context, R.color.wind_speed_color))
            updatePadding(bottom = 4.dp)
            text = "--"
        }

    private val bottomTextWindSpeed =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setTextColor(ContextCompat.getColor(context, R.color.wind_speed_color))
            text = "风速"
        }

    // 气压
    private val pressureText =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            updatePadding(bottom = 4.dp)
            setTextColor(ContextCompat.getColor(context, R.color.pressure_color))
            text = "--"
        }

    private val bottomTextPressure =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setTextColor(ContextCompat.getColor(context, R.color.pressure_color))
            text = "气压"
        }

    //   体感温度
    private val feelTemp =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            updatePadding(bottom = 4.dp)
            setTextColor(ContextCompat.getColor(context, R.color.feel_temp_color))
            text = "--"
        }

    private val bottomTextFeelTemp =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setTextColor(ContextCompat.getColor(context, R.color.feel_temp_color))
            text = "体感温度"
        }

    // 描述文本
    private val descText =
        AppCompatTextView(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setTextAppearance(R.style.TextAppearance_Material3_LabelLarge)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
            text = "--"
            updatePadding(0, 0, 0, 12.dp)
        }

    init {
        setPadding(15.dp, 15.dp, 15.dp, 20.dp)
        addView(windSpeedText)
        addView(waterText)
        addView(pressureText)
        addView(descText)
        addView(feelTemp)
        addView(bottomTextWater)
        addView(bottomTextWindSpeed)
        addView(bottomTextPressure)
        addView(bottomTextFeelTemp)
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {
        descText.text = weather.descriptionForToday
        // 带入体感温度
        val feelTempText = SpannableString("${weather.todayWeather.feelTemp}℃")
        feelTempText.setSpan(
            AbsoluteSizeSpan(28.dp),
            0,
            feelTempText.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        feelTempText.setSpan(
            AbsoluteSizeSpan(16.dp),
            Math.max(feelTempText.length - 1, 0),
            feelTempText.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        feelTemp.text = feelTempText
        // 带入湿度
        val waterText = SpannableString("${weather.todayWeather.water}%")
        waterText.setSpan(
            AbsoluteSizeSpan(28.dp),
            0,
            waterText.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        waterText.setSpan(
            AbsoluteSizeSpan(16.dp),
            Math.max(waterText.length - 1, 0),
            waterText.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        this.waterText.text = waterText
        // 带入风速
        val windSpeedText = SpannableString("${weather.todayWeather.windSpeed}m/s")
        windSpeedText.setSpan(
            AbsoluteSizeSpan(28.dp),
            0,
            windSpeedText.length - 3,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        windSpeedText.setSpan(
            AbsoluteSizeSpan(16.dp),
            Math.max(windSpeedText.length - 3, 0),
            windSpeedText.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        this.windSpeedText.text = windSpeedText
        //  带入气压
        val pressureText = SpannableString("${weather.todayWeather.windPa}Pa")
        pressureText.setSpan(
            AbsoluteSizeSpan(28.dp),
            0,
            pressureText.length - 2,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        pressureText.setSpan(
            AbsoluteSizeSpan(16.dp),
            Math.max(pressureText.length - 2, 0),
            pressureText.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE,
        )
        this.pressureText.text = pressureText
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        descText.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            descText.defaultHeightMeasureSpec(this),
        )
        feelTemp.autoMeasure()
        waterText.autoMeasure()
        windSpeedText.autoMeasure()
        pressureText.autoMeasure()
        bottomTextWater.autoMeasure()
        bottomTextWindSpeed.autoMeasure()
        bottomTextPressure.autoMeasure()
        bottomTextFeelTemp.autoMeasure()
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(
                paddingTop +
                    paddingBottom +
                    descText.measuredHeightWithMargins +
                    windSpeedText.measuredHeightWithMargins +
                    bottomTextWindSpeed.measuredHeightWithMargins,
                heightMeasureSpec,
            ),
        )
    }

    override fun onLayout(
        change: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        descText.layout(
            paddingLeft,
            paddingTop,
        )

        val itemWidth = (measuredWidth - paddingLeft - paddingRight) / 4f
        windSpeedText.layout(
            (itemWidth / 2f + paddingLeft - windSpeedText.width / 2f).toInt(),
            descText.bottom,
        )
        bottomTextWindSpeed.layout(
            bottomTextWindSpeed.toViewHorizontalCenter(windSpeedText),
            windSpeedText.bottom,
        )

        waterText.layout(
            (itemWidth / 2f + paddingLeft + itemWidth - waterText.width / 2f).toInt(),
            descText.bottom,
        )
        bottomTextWater.layout(
            bottomTextWater.toViewHorizontalCenter(waterText),
            waterText.bottom,
        )

        feelTemp.layout(
            (itemWidth / 2f + paddingLeft + itemWidth * 2 - feelTemp.width / 2f).toInt(),
            descText.bottom,
        )
        bottomTextFeelTemp.layout(
            bottomTextFeelTemp.toViewHorizontalCenter(feelTemp),
            feelTemp.bottom,
        )

        pressureText.layout(
            (itemWidth / 2f + paddingLeft + itemWidth * 3 - pressureText.width / 2f).toInt(),
            descText.bottom,
        )

        bottomTextPressure.layout(
            bottomTextPressure.toViewHorizontalCenter(pressureText),
            pressureText.bottom,
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.NOW_WEATHER.code

    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

    override fun resetAnim() {
        super.resetAnim()
    }


    override fun startEnterAnim() {
        super.startEnterAnim()
    }
}
