package me.spica.spicaweather2.view.weather_detail_card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.databinding.CardHourlyWeatherBinding
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.tools.hide
import me.spica.spicaweather2.tools.show

/**
 * 小时级的天气信息卡片
 */
class HourWeatherCard : CardLinearlayout, SpicaWeatherCard {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = CardHourlyWeatherBinding.inflate(LayoutInflater.from(context), this, true)




    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 1
    override var hasInScreen: Boolean = false

    init {
        resetAnim()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData(weather: Weather) {
        val items = weather.hourlyWeather

        val themeColor = weather.getWeatherType().getThemeColor()

        binding.cardName.setTextColor(themeColor)
        binding.layoutLoading.hide()
        weather.hourlyWeather.toList().sortedBy {
            it.temp
        }.apply {
            binding.hourForecastView.setData(weather)
        }

        doOnMainThreadIdle({
            binding.tipDesc.text = weather.descriptionForToday
            if (weather.descriptionForToday.isNullOrEmpty()) {
                binding.tipDesc.hide()
            } else {
                binding.tipDesc.show()
            }
        })
    }

    override fun startEnterAnim() {
        super.startEnterAnim()
        binding.hourForecastView.startAnim(150)
    }
}
