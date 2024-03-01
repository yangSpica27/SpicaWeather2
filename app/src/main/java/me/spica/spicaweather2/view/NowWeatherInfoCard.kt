package me.spica.spicaweather2.view

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getAnimRes
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.view_group.AViewGroup
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class NowWeatherInfoCard(context: Context) : AViewGroup(context = context), SpicaWeatherCard {

    init {
        setBackgroundResource(R.drawable.bg_card)
        ViewCompat.setElevation(this, 4.dp.toFloat())
    }

    private val lottieAnimationView = LottieAnimationView(context).apply {
        layoutParams = LayoutParams(
            100.dp,
            100.dp
        )
        setAnimation(R.raw.windy)
        repeatCount = LottieDrawable.INFINITE
        repeatMode = LottieDrawable.RESTART
    }

    private val currentTemperature = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(
                top = 8.dp
            )
        }
        setTextAppearance(R.style.TextAppearance_Material3_DisplayLarge)
        setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
        text = "0℃"
    }

    private val temperatureDescText = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(
                top = 12.dp
            )
        }
        setTextAppearance(R.style.TextAppearance_Material3_TitleMedium)
        setTextColor(ContextCompat.getColor(context, R.color.textColorPrimaryLight))
        text = "多云 体感--°C -℃ | -℃"
    }

    private val divider = View(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            2.dp
        ).apply {
            topMargin = 20.dp
        }
        setBackgroundColor(ContextCompat.getColor(context, R.color.line_divider))
    }

    private val waterText = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(
                top = 20.dp
            )
        }
        gravity = Gravity.CENTER
        setCompoundDrawables(
            ContextCompat.getDrawable(context, R.drawable.ic_water)?.apply {
                setBounds(0, 0, 20.dp, 20.dp)
            },
            null, null, null
        )
        setTextAppearance(R.style.TextAppearance_Material3_TitleLarge)
        text = "-%"
    }

    private val pressureText = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(
                top = 20.dp
            )
        }
        setTextAppearance(R.style.TextAppearance_Material3_TitleLarge)
        setCompoundDrawables(
            ContextCompat.getDrawable(context, R.drawable.ic_pressure)?.apply {
                setBounds(0, 0, 20.dp, 20.dp)
            },
            null, null, null
        )
        gravity = Gravity.CENTER
        text = "--pa"
    }

    private val windSpeed = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(
                top = 20.dp
            )
        }
        setTextAppearance(R.style.TextAppearance_Material3_TitleLarge)
        setCompoundDrawables(
            ContextCompat.getDrawable(context, R.drawable.ic_wind2)?.apply {
                setBounds(0, 0, 20.dp, 20.dp)
            },
            null, null, null
        )
        gravity = Gravity.CENTER
        text = "--m/s"
    }

    init {
        setPadding(14.dp, 0, 14.dp, 20.dp)
        addView(currentTemperature)
        addView(temperatureDescText)
        addView(windSpeed)
        addView(waterText)
        addView(pressureText)
        addView(lottieAnimationView)
        addView(divider)
    }

    private val nowCardXY = intArrayOf(0, 0)
    fun getNowCardTop(): Int {
        currentTemperature.getLocationOnScreen(nowCardXY)
        return nowCardXY[1]
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {
        lottieAnimationView.setAnimation(WeatherCodeUtils.getWeatherCode(weather.todayWeather.iconId).getAnimRes())
        lottieAnimationView.playAnimation()
        currentTemperature.text = "${weather.todayWeather.temp}℃"
        temperatureDescText.text = "${weather.todayWeather.weatherName} 体感${weather.todayWeather.feelTemp}℃"
        waterText.text = "${weather.todayWeather.water}%"
        windSpeed.text = "${weather.todayWeather.windSpeed}m/s"
        pressureText.text = "${weather.todayWeather.windPa}pa"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        currentTemperature.autoMeasure()
        temperatureDescText.autoMeasure()
        lottieAnimationView.autoMeasure()
        divider.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            divider.defaultHeightMeasureSpec(this)
        )
        waterText.autoMeasure()
        pressureText.autoMeasure()
        windSpeed.autoMeasure()
        setMeasuredDimension(
            measuredWidth,
            currentTemperature.measuredHeightWithMargins +
                temperatureDescText.measuredHeightWithMargins +
                divider.measuredHeightWithMargins +
                waterText.measuredHeightWithMargins +
                paddingBottom + paddingTop
        )
    }

    override fun onLayout(change: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        currentTemperature.layout(
            paddingRight,
            paddingTop + currentTemperature.marginTop,
            true
        )
        temperatureDescText.layout(
            paddingRight,
            currentTemperature.bottom + temperatureDescText.marginTop,
            true
        )

        divider.layout(
            paddingLeft,
            temperatureDescText.bottom + divider.marginTop,
        )
        lottieAnimationView.layout(
            paddingLeft,
            ((divider.top + currentTemperature.top) / 2f - lottieAnimationView.height / 2f).toInt()
        )

        waterText.layout(
            paddingLeft,
            divider.bottom + waterText.marginTop
        )
        pressureText.layout(
            pressureText.toHorizontalCenter(this),
            divider.bottom + pressureText.marginTop
        )
        windSpeed.layout(
            paddingRight,
            divider.bottom + windSpeed.marginTop,
            true
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.NOW_WEATHER.code

    override var hasInScreen: Boolean = false
}
