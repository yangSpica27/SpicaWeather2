package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class TipsLayout(context: Context) : AViewGroup(context), SpicaWeatherCard {


    // SPT
    private val sptIcon = AppCompatImageView(context)

    private val sptTitle = AppCompatImageView(context)

    private val aptDesc = AppCompatTextView(context)


    // CLOTHES
    private val clothesIcon = AppCompatImageView(context)

    private val clothesTitle = AppCompatImageView(context)

    private val clothesDesc = AppCompatTextView(context)

    // AIR
    private val airIcon = AppCompatImageView(context)

    private val  airTitle = AppCompatImageView(context)

    private val  airDesc = AppCompatTextView(context)

    // CAR



    override fun bindData(weather: Weather) {

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {

    }


    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = 0

    override var hasInScreen: Boolean = false


}