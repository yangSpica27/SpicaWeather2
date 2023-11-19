package me.spica.spicaweather2.view.weather_detail_card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.databinding.CardAirBinding
import me.spica.spicaweather2.persistence.entity.weather.Weather

/**
 * 空气质量卡片
 */
class AirCard : ConstraintLayout, SpicaWeatherCard {

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 2
    override var hasInScreen: Boolean = false

    private val binding = CardAirBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        resetAnim()
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {
        val themeColor = weather.getWeatherType().getThemeColor()
        binding.tvTitle.setTextColor(themeColor)
        binding.progressView.bindProgress(weather.air.aqi,weather.air.category)
        binding.tvC0Value.text = "${weather.air.co}微克/m³"
        binding.tvNo2Value.text = "${weather.air.no2}微克/m³"
        binding.tvPm25Value.text = "${weather.air.pm2p5}微克/m³"
        binding.tvSo2Value.text = "${weather.air.so2}微克/m³"
    }


    override fun startEnterAnim() {
        super.startEnterAnim()
        binding.progressView.startAnim()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}