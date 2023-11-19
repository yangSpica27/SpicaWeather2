package me.spica.spicaweather2.view.weather_detail_card

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.databinding.CardNowWeatherBinding
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.tools.getStatusBarHeight
import me.spica.spicaweather2.tools.hide
import me.spica.spicaweather2.tools.show
import java.text.SimpleDateFormat
import java.util.*


/**
 *  用于展示现在的天气
 */
class NowWeatherCard : ConstraintLayout, SpicaWeatherCard {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding: CardNowWeatherBinding = CardNowWeatherBinding.inflate(LayoutInflater.from(context), this, true)

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 0
    override var hasInScreen: Boolean = false


    init {
        resetAnim()
        binding.root.updateLayoutParams<MarginLayoutParams> {
            topMargin = context.getStatusBarHeight()
        }
    }

    private val numAnim = ValueAnimator.ofInt(0, 0)
        .apply {
            duration = 1050L
            interpolator = DecelerateInterpolator(1f)
        }

    // 12:00
    private val sdfAfter = SimpleDateFormat("更新于 HH:mm", Locale.CHINA)

    private val showInAnimation by lazy { AnimationUtils.loadAnimation(context, R.anim.in_bottom) }




    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {


        val nowWeatherBean = weather.todayWeather
        val themeColor = weather.getWeatherType().getThemeColor()
        doOnMainThreadIdle({
            with(numAnim) {
                if (isRunning) cancel()
                setIntValues(0, weather.todayWeather.temp)
                removeAllListeners()
                addUpdateListener {
                    binding.tvCurrentTemp.text = "${it.animatedValue as Int}℃"
                    setTextViewStyles(binding.tvCurrentTemp)
                }
                doOnEnd {
                    if (weather.alerts.isEmpty()) {
                        binding.tvAlertTitle.hide()
                    } else {
                        binding.tvAlertTitle.text = weather.alerts[0].title
                        binding.tvAlertTitle.setTextColor(Color.WHITE)
                        binding.tvAlertTitle.show()
                    }
                }
                start()
            }

            binding.tvCurrentTemp.setOnClickListener {
                binding.tvCurrentTemp.startAnimation(showInAnimation)
            }


            binding.tvWaterValue.text = nowWeatherBean.water.toString() + "%"
            binding.tvWindPaValue.text = nowWeatherBean.windPa.toString() + "hPa"
            binding.tvWindSpeedValue.text = nowWeatherBean.windSpeed.toString() + "km/h"
        }, 200)


    }

    private fun setTextViewStyles(textView: TextView) {
        val mLinearGradient = LinearGradient(
            0f,
            0f, 0f, textView.height * 1f,
            Color.WHITE,
            Color.parseColor("#80FFFFFF"),
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = mLinearGradient
        textView.postInvalidateOnAnimation()
    }

    override fun resetAnim() {
        super.resetAnim()
//    animatorView.alpha = 1f
    }
}
