package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import androidx.core.widget.CompoundButtonCompat
import com.google.android.material.animation.AnimatorSetCompat
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getDrawable
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.tools.hide
import me.spica.spicaweather2.tools.show

class ItemCityManagerLayout(context: Context) : AViewGroup(context) {

    private val iconSort = AppCompatImageView(context).apply {
        setImageResource(R.drawable.ic_drag)
        layoutParams = LayoutParams(
            26.dp, 26.dp
        ).apply {
            updateMargins(left = 14.dp, right = 20.dp)
        }
        alpha = 0f
    }

    private val cityName = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_TitleLarge)
        setTextColor(ContextCompat.getColor(context, R.color.white))
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text = "城市"
    }

    private val weatherName = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_TitleMedium)
        setTextColor(ContextCompat.getColor(context, R.color.white))
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(top = 4.dp)
        }
        text = "天气"
    }

    private val iconSelect = MaterialRadioButton(context).apply {
        layoutParams = LayoutParams(
            48.dp, 48.dp
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
            0, 20.dp, 14.dp, 20.dp
        )
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 14.dp
            rightMargin = 14.dp
        }
//        addView(backgroundView)
        addView(iconSort)
        addView(cityName)
        addView(weatherName)
        addView(iconSelect)
//        setBackgroundResource(R.drawable.bg_manager_city_item)
    }

    var isSelectable = false
        @Synchronized
        set(value) {
            if (field != value) {
                if (value) {
                    iconSort.animate().alpha(1f).start()
                    iconSelect.animate().alpha(1f).start()
                    weatherName.animate().translationX(0f).start()
                    cityName.animate().translationX(0f).start()
                } else {
                    iconSort.animate().alpha(0f)
                        .setDuration(255)
                        .withEndAction {
                            weatherName.animate()
                                .translationX(-iconSort.measuredWidthWithMargins.toFloat() + 14.dp)
                                .start()
                            cityName.animate()
                                .translationX(-iconSort.measuredWidthWithMargins.toFloat() + 14.dp)
                                .start()
                        }.start()
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
            setBackgroundResource(R.drawable.bg_manager_city_item)
        }

        cityWithWeather.weather?.let { weather ->
            background =
                WeatherCodeUtils.getWeatherCode(weather.todayWeather.iconId).getDrawable().apply {
                    cornerRadius = 16.dp.toFloat()
                }
        }

        cityName.text = cityWithWeather.city.cityName
        weatherName.text =
            "${cityWithWeather.weather?.todayWeather?.weatherName} ${cityWithWeather.weather?.todayWeather?.temp}℃"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        iconSort.autoMeasure()
        iconSelect.autoMeasure()
//        backgroundView.autoMeasure()
        cityName.measure(
            (measuredWidth - iconSort.measuredWidthWithMargins - iconSelect.measuredWidthWithMargins).toExactlyMeasureSpec(),
            weatherName.defaultHeightMeasureSpec(this)
        )
        weatherName.measure(
            (measuredWidth - iconSort.measuredWidthWithMargins - iconSelect.measuredWidthWithMargins).toExactlyMeasureSpec(),
            weatherName.defaultHeightMeasureSpec(this)
        )
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(
                cityName.measuredHeightWithMargins + weatherName.measuredHeightWithMargins + paddingTop + paddingBottom,
                heightMeasureSpec
            )
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
//        backgroundView.layout(0, 0)
        iconSort.layout(
            paddingLeft + iconSort.marginLeft, iconSort.toVerticalCenter(this)
        )
        cityName.layout(
            iconSort.right + cityName.marginLeft + iconSort.marginRight,
            paddingTop + cityName.marginTop
        )
        weatherName.layout(
            iconSort.right + cityName.marginLeft + iconSort.marginRight,
            cityName.bottom + weatherName.marginTop
        )
        iconSelect.layout(
            paddingRight, iconSelect.toVerticalCenter(this),
            true
        )
    }


    private val clipPath = Path()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val roundedCorner = 24.dp * 1f
        clipPath.reset()
        clipPath.moveTo(roundedCorner, 0f);
        clipPath.lineTo(width - roundedCorner, 0f);
        clipPath.quadTo(width * 1f, 0f, width * 1f, roundedCorner)
        clipPath.lineTo(width * 1f, height - roundedCorner)
        clipPath.quadTo(width * 1f, height * 1f, width - roundedCorner, height * 1f)
        clipPath.lineTo(roundedCorner, height * 1f)
        clipPath.quadTo(0f, height * 1f, 0f, height - roundedCorner)
        clipPath.lineTo(0f, roundedCorner)
        clipPath.quadTo(0f, 0f, roundedCorner, 0f)
    }


    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(clipPath)
        super.dispatchDraw(canvas)
    }
}
