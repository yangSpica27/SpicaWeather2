package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.HomeCardType
import me.spica.spicaweather2.common.WeatherType
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.minute_rain.MinuteRainView
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

class MinuteWeatherCard(
  context: Context,
) : AViewGroup(context = context),
  SpicaWeatherCard {
  private val titleText =
    AppCompatTextView(context).apply {
      layoutParams =
        MarginLayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
          updateMargins(bottom = 8.dp)
        }
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelLarge)
      setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
      setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
      text = "分钟级降水"
      addView(this)
    }

  init {
    isFocusable = false
    isClickable = false
    layoutParams =
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      )
    setPadding(14.dp, 12.dp, 14.dp, 12.dp)
    setBackgroundResource(R.drawable.bg_card)
  }

  private val rainView =
    MinuteRainView(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
      addView(this)
    }

  private val nowCardXY = intArrayOf(0, 0)

  fun getNowCardTop(): Int {
    getLocationInWindow(nowCardXY)
    return nowCardXY[1] +
        if (visibility == View.VISIBLE) {
          0
        } else {
          height + 16.dp
        }
  }

  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int,
  ) {
    titleText.layout(paddingLeft, paddingTop)
    rainView.layout(paddingLeft, titleText.bottom + titleText.marginBottom)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    rainView.autoMeasure()
    titleText.autoMeasure()
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(
        paddingTop + paddingBottom + rainView.measuredHeightWithMargins + titleText.measuredHeightWithMargins,
        heightMeasureSpec,
      ),
    )
  }

  override var animatorView: View = this

  override var enterAnim: AnimatorSet = AnimatorSet()

  override var index: Int = HomeCardType.MINUTE_WEATHER.code

  override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

  override fun bindData(weather: Weather) {
    rainView.setData(weather.minutelies.map { (it.precip.toFloatOrNull() ?: 0f) })
    rainView.startAnim(600)
    visibility =
      if (weather.getWeatherType() == WeatherType.WEATHER_RAINY || weather.getWeatherType() == WeatherType.WEATHER_THUNDERSTORM) {
        View.VISIBLE
      } else {
        View.INVISIBLE
      }
  }
}
