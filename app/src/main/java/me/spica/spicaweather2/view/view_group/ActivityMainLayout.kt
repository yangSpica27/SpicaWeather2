package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import me.spica.spicaweather2.view.weather_bg.WeatherBackgroundView

class ActivityMainLayout(
  context: Context,
) : AViewGroup(context) {
  val backgroundView =
    WeatherBackgroundView(context)
      .apply {
        layoutParams =
          LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
          )
      }

  val currentWeatherLayout =
    CurrentWeatherLayout(context).apply {
      layoutParams =
        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

  val viewPager2 =
    ViewPager2(context).apply {
      layoutParams =
        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

  val mainTitleLayout =
    MainTitleLayout(context).apply {
      layoutParams =
        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

  init {
    addView(backgroundView)
    addView(viewPager2)
    addView(mainTitleLayout)
    addView(currentWeatherLayout)
    layoutParams =
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
      )
    setBackgroundColor(Color.WHITE)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    children.forEach {
      it.autoMeasure()
    }
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(measuredHeight, heightMeasureSpec),
    )
  }

  override fun onLayout(
    p0: Boolean,
    p1: Int,
    p2: Int,
    p3: Int,
    p4: Int,
  ) {
    backgroundView.layout(0, 0)
    viewPager2.layout(0, 0)
    mainTitleLayout.layout(0, 0)
    currentWeatherLayout.layout(0, mainTitleLayout.bottom)
  }
}
