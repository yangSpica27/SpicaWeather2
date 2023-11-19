package me.spica.spicaweather2.view.weather_detail_card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.databinding.CardLifeIndexBinding
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.doOnMainThreadIdle


/**
 * 生活指数卡
 */
class TipsCard : RelativeLayout, SpicaWeatherCard {

    private val tipAdapter = TipAdapter()

    private val binding = CardLifeIndexBinding.inflate(LayoutInflater.from(context), this, true)
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 4
    override var hasInScreen: Boolean = false

    init {
        resetAnim()
        binding.rvTip.adapter = tipAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData(weather: Weather) {
        val themeColor = weather.getWeatherType().getThemeColor()
        tipAdapter.themeColor = themeColor
        binding.cardName.setTextColor(themeColor)
        val items = weather.lifeIndexes
        tipAdapter.items.clear()
        tipAdapter.items.addAll(items)
        doOnMainThreadIdle({
            tipAdapter.notifyDataSetChanged()
        })
    }


}
