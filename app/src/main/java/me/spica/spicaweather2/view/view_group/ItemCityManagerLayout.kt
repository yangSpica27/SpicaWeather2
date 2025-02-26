package me.spica.spicaweather2.view.view_group

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import androidx.core.widget.CompoundButtonCompat
import com.google.android.material.radiobutton.MaterialRadioButton
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.CityWithWeather


class ItemCityManagerLayout(
  context: Context,
) : AViewGroup(context) {

  private val backgroundView = object : View(context) {

    init {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
//      stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.touch_raise)
    }

    private val colorAnim = ValueAnimator.ofFloat(
      0f, 1f
    ).apply {
      duration = 300
      addUpdateListener {
        postInvalidateOnAnimation()
      }
    }

    override fun onDetachedFromWindow() {
      super.onDetachedFromWindow()
      colorAnim.cancel()
    }

    private var lastColor = context.getColor(R.color.light_blue_600)

    private var currentColor = context.getColor(R.color.light_blue_600)

    fun setColor(color: Int) {
      lastColor = currentColor
      currentColor = color
      colorAnim.cancel()
      colorAnim.start()
    }

    private val paint = Paint().apply {
      isAntiAlias = true
      style = Paint.Style.FILL
    }

    private val rect = android.graphics.RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
      super.onSizeChanged(w, h, oldw, oldh)
      rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }


    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      if (colorAnim.isRunning) {
        paint.color = lastColor
        canvas.drawRoundRect(rect, 8.dp.toFloat(), 8.dp.toFloat(), paint)
      }
      paint.color = currentColor
      canvas.drawRoundRect(
        rect.left + width - width * colorAnim.animatedValue as Float,
        rect.top,
        rect.right,
        rect.bottom,
        8.dp.toFloat(), 8.dp.toFloat(), paint
      )
    }
  }

  private val iconSort =
    AppCompatImageView(context).apply {
      setImageResource(R.drawable.ic_drag)
      layoutParams =
        LayoutParams(
          26.dp,
          26.dp,
        ).apply {
          updateMargins(right = 20.dp)
        }
      alpha = 0f
    }

  private val cityName =
    AppCompatTextView(context).apply {
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
      setTextColor(ContextCompat.getColor(context, R.color.white))
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
      text = "--"
      typeface =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          Typeface.create(Typeface.DEFAULT, 500, false)
        } else {
          Typeface.DEFAULT
        }
      textSize = 20f
    }

  private val weatherName =
    AppCompatTextView(context).apply {
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium)
      setTextColor(ContextCompat.getColor(context, R.color.white))
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
          updateMargins(top = 12.dp)
        }
      textSize = 16f
      typeface =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          Typeface.create(Typeface.DEFAULT, 400, false)
        } else {
          Typeface.DEFAULT
        }
      text = "天气"
    }

  private val tempTextView =
    AppCompatTextView(context).apply {
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
      setTextColor(ContextCompat.getColor(context, R.color.white))
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
      text = "--"
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        typeface = Typeface.create(Typeface.DEFAULT, 900, false)
      }
      textSize = 38f
      setPadding(0, 0, 10.dp, 0)
    }

  private val iconSelect =
    MaterialRadioButton(context).apply {
      layoutParams =
        LayoutParams(
          48.dp,
          48.dp,
        ).apply {
          updateMargins(left = 14.dp, right = 14.dp)
        }
      isClickable = false
      alpha = 0f
      CompoundButtonCompat.setButtonTintList(this, ColorStateList.valueOf(Color.WHITE))
    }

  init {
    isClickable = true
    setPadding(
      20.dp,
      20.dp,
      14.dp,
      20.dp,
    )
    clipToPadding = false
    layoutParams =
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      ).apply {
        leftMargin = 14.dp
        rightMargin = 14.dp
      }
    addView(backgroundView)
    addView(iconSort)
    addView(cityName)
    addView(weatherName)
    addView(iconSelect)
    addView(tempTextView)
//        setBackgroundResource(R.drawable.bg_manager_city_item)
  }

  var isSelectable = false
    @Synchronized
    set(value) {
      if (field != value) {
        if (value) {
          iconSort.animate().alpha(1f).start()
          iconSelect.animate().alpha(1f).start()
          weatherName
            .animate()
            .translationX((iconSort.marginRight + iconSort.width).toFloat())
            .start()
          cityName
            .animate()
            .translationX((iconSort.marginRight + iconSort.width).toFloat())
            .start()
          tempTextView
            .animate()
            .translationX(-iconSelect.width * 1f - 8.dp)
            .start()
        } else {
          iconSort.animate().alpha(0f).start()
          weatherName
            .animate()
            .translationX(0f)
            .start()
          tempTextView
            .animate()
            .translationX(0f)
            .start()
          cityName
            .animate()
            .translationX(0f)
            .start()
          iconSelect.animate().alpha(0f).start()
        }
      }
      field = value
    }

  var isSelect = false
    set(value) {
      field = value
      iconSelect.isChecked = value
    }

  @SuppressLint("SetTextI18n")
  fun setData(cityWithWeather: CityWithWeather) {

    if (cityWithWeather.weather == null) {
      backgroundView.setColor(context.getColor(R.color.light_blue_600))
    }

    cityWithWeather.weather?.let { weather ->
      val themeColor = WeatherCodeUtils.getWeatherCode(weather.todayWeather.iconId).getThemeColor()
      setTag(R.id.dn_theme_color, themeColor)
      backgroundView.setColor(themeColor)
    }

    cityName.text = cityWithWeather.city.cityName
    if (cityWithWeather.weather?.todayWeather == null) {
      weatherName.text = "暂无数据"
      tempTextView.text = "--"
      return
    }
    weatherName.text =
      "${cityWithWeather.weather?.todayWeather?.weatherName} 体感${cityWithWeather.weather?.todayWeather?.feelTemp}℃"
    tempTextView.text = "${cityWithWeather.weather?.todayWeather?.temp}℃"
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    iconSort.autoMeasure()
    iconSelect.autoMeasure()
    tempTextView.autoMeasure()
//        backgroundView.autoMeasure()
    cityName.measure(
      (measuredWidth - iconSort.measuredWidthWithMargins - iconSelect.measuredWidthWithMargins).toExactlyMeasureSpec(),
      weatherName.defaultHeightMeasureSpec(this),
    )
    weatherName.measure(
      (measuredWidth - iconSort.measuredWidthWithMargins - iconSelect.measuredWidthWithMargins).toExactlyMeasureSpec(),
      weatherName.defaultHeightMeasureSpec(this),
    )
    backgroundView.measure(
      backgroundView.defaultWidthMeasureSpec(this),
      (
          cityName.measuredHeightWithMargins + weatherName.measuredHeightWithMargins + paddingTop + paddingBottom)
        .toExactlyMeasureSpec(),
    )
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(
        cityName.measuredHeightWithMargins +
            weatherName.measuredHeightWithMargins +
            paddingTop
            + paddingBottom,
        heightMeasureSpec,
      ),
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
    iconSort.layout(
      paddingLeft + iconSort.marginLeft,
      iconSort.toVerticalCenter(this),
    )
    cityName.layout(
      cityName.marginLeft + paddingLeft,
      paddingTop + cityName.marginTop,
    )
    weatherName.layout(
      cityName.marginLeft + paddingLeft,
      cityName.bottom + weatherName.marginTop,
    )
    iconSelect.layout(
      paddingRight,
      iconSelect.toVerticalCenter(this),
      true,
    )
    tempTextView.layout(
      width - tempTextView.measuredWidth - paddingRight,
      height / 2 - tempTextView.measuredHeight / 2,
    )
  }


}
