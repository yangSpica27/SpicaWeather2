package me.spica.spicaweather2.view.view_group

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.Build
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import androidx.core.widget.CompoundButtonCompat
import com.google.android.material.radiobutton.MaterialRadioButton
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getDrawable
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.CityWithWeather

class ItemCityManagerLayout(context: Context) : AViewGroup(context) {

    private val iconSort = AppCompatImageView(context).apply {
        setImageResource(R.drawable.ic_drag)
        layoutParams = LayoutParams(
            26.dp, 26.dp
        ).apply {
            updateMargins(right = 20.dp)
        }
        alpha = 0f
    }

    private val cityName = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_TitleLarge)
        setTextColor(ContextCompat.getColor(context, R.color.white))
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text = "--"
        typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface.create(Typeface.DEFAULT, 600, false)
        } else {
            Typeface.DEFAULT
        }
        textSize = 20f
    }

    private val weatherName = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_TitleMedium)
        setTextColor(ContextCompat.getColor(context, R.color.white))
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            updateMargins(top = 12.dp)
        }
        textSize = 16f
        typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface.create(Typeface.DEFAULT, 400, false)
        } else {
            Typeface.DEFAULT
        }
        text = "天气"
    }

    private val tempTextView = AppCompatTextView(context).apply {
        setTextAppearance(R.style.TextAppearance_Material3_TitleLarge)
        setTextColor(ContextCompat.getColor(context, R.color.white))
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text = "--"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            typeface = Typeface.create(Typeface.DEFAULT, 900, false)
        }
        textSize = 38f
        setPadding(0, 0, 10.dp, 0)
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
            20.dp, 20.dp, 14.dp, 20.dp
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
                    weatherName.animate()
                        .translationX((iconSort.marginRight + iconSort.width).toFloat()).start()
                    cityName.animate()
                        .translationX((iconSort.marginRight + iconSort.width).toFloat()).start()
                    tempTextView.animate()
                        .translationX(-iconSelect.width * 1f - 8.dp).start()

                } else {
                    iconSort.animate().alpha(0f).start()
                    weatherName.animate()
                        .translationX(0f)
                        .start()
                    tempTextView.animate()
                        .translationX(0f)
                        .start()
                    cityName.animate()
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
            setBackgroundResource(R.drawable.bg_manager_city_item)
        }

        cityWithWeather.weather?.let { weather ->
            background = ContextCompat.getDrawable(
                context,
                R.drawable.bg_manager_city_item
            )?.apply {
                colorFilter = PorterDuffColorFilter(
                    WeatherCodeUtils.getWeatherCode(weather.todayWeather.iconId).getThemeColor(),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }

        cityName.text = cityWithWeather.city.cityName
        weatherName.text =
            "${cityWithWeather.weather?.todayWeather?.weatherName} 体感${cityWithWeather.weather?.todayWeather?.feelTemp}℃"
        tempTextView.text = "${cityWithWeather.weather?.todayWeather?.temp}℃"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        iconSort.autoMeasure()
        iconSelect.autoMeasure()
        tempTextView.autoMeasure()
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
            cityName.marginLeft + paddingLeft,
            paddingTop + cityName.marginTop
        )
        weatherName.layout(
            cityName.marginLeft + paddingLeft,
            cityName.bottom + weatherName.marginTop
        )
        iconSelect.layout(
            paddingRight, iconSelect.toVerticalCenter(this),
            true
        )
        tempTextView.layout(
            width - tempTextView.measuredWidth - paddingRight,
            height / 2 - tempTextView.measuredHeight / 2
        )
    }


//    private val clipPath = Path()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        val roundedCorner = 2.dp * 1f
//        clipPath.reset()
//        clipPath.moveTo(roundedCorner, 0f);
//        clipPath.lineTo(width - roundedCorner, 0f);
//        clipPath.quadTo(width * 1f, 0f, width * 1f, roundedCorner)
//        clipPath.lineTo(width * 1f, height - roundedCorner)
//        clipPath.quadTo(width * 1f, height * 1f, width - roundedCorner, height * 1f)
//        clipPath.lineTo(roundedCorner, height * 1f)
//        clipPath.quadTo(0f, height * 1f, 0f, height - roundedCorner)
//        clipPath.lineTo(0f, roundedCorner)
//        clipPath.quadTo(0f, 0f, roundedCorner, 0f)
    }


    override fun dispatchDraw(canvas: Canvas) {
//        canvas.clipPath(clipPath)
        super.dispatchDraw(canvas)
    }
}
