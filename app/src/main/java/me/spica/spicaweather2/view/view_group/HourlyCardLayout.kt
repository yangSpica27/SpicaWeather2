package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.HomeCardType
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.line.HourlyLineView
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

class HourlyCardLayout(
  context: Context,
) : AViewGroup(context),
  SpicaWeatherCard {
  private val hourlyLineView =
    HourlyLineView(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

  private val titleTextView =
    AppCompatTextView(context).apply {
      text = "小时级别天气"
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
          it.updateMargins(
            left = 14.dp,
            top = 12.dp,
          )
        }
      setTextAppearance(
        context,
        com.google.android.material.R.style.TextAppearance_Material3_TitleMedium
      )
      setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
      typeface = Typeface.DEFAULT_BOLD
    }

  init {
    setPadding(0, 12.dp, 0, 12.dp)
    setBackgroundResource(R.drawable.bg_card)
    addView(titleTextView)
    addView(hourlyLineView)
    layoutParams =
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      ).apply {
        leftMargin = 14.dp
        rightMargin = 14.dp
      }
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    hourlyLineView.autoMeasure()
    titleTextView.autoMeasure()
    setMeasuredDimension(
      measuredWidth,
      paddingTop + paddingBottom +
          hourlyLineView.measuredHeightWithMargins +
          titleTextView.measuredHeightWithMargins,
    )
  }

  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int,
  ) {
    titleTextView.layout(
      paddingLeft + titleTextView.marginLeft,
      paddingTop,
    )
    hourlyLineView.layout(
      paddingLeft,
      titleTextView.bottom,
    )
  }

  override var animatorView: View = this

  override var enterAnim: AnimatorSet = AnimatorSet()
  override var index: Int = HomeCardType.HOUR_WEATHER.code
  override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

  override fun bindData(weather: Weather) {
    hourlyLineView.setData(weather.hourlyWeather)
  }
}
