package me.spica.spicaweather2.view.view_group

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getDrawable
import me.spica.spicaweather2.persistence.entity.CityWithWeather

class ItemCityManagerLayout(context: Context) : AViewGroup(context) {

    private val iconSort = AppCompatImageView(context).apply {
        setImageResource(R.drawable.ic_drag)
        layoutParams = LayoutParams(
            30.dp, 30.dp
        ).apply {
            updateMargins(left = 14.dp, right = 20.dp)
        }
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

    val iconDelete = AppCompatImageView(context).apply {
        setImageResource(R.drawable.ic_close_small)
        layoutParams = LayoutParams(
            24.dp, 24.dp
        ).apply {
            updateMargins(left = 14.dp, right = 14.dp)
        }
    }

    init {
        isClickable = true
        setPadding(
            0, 14.dp, 14.dp, 14.dp
        )
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            leftMargin = 14.dp
            rightMargin = 14.dp
        }
        addView(iconSort)
        addView(cityName)
        addView(weatherName)
        addView(iconDelete)
        setBackgroundResource(R.drawable.bg_manager_city_item)
    }

    @SuppressLint("SetTextI18n")
    fun setData(cityWithWeather: CityWithWeather) {

        if (cityWithWeather.weather == null) {
            setBackgroundResource(R.drawable.bg_manager_city_item)
        }

        cityWithWeather.weather?.let { weather ->
            background = WeatherCodeUtils.getWeatherCode(weather.todayWeather.iconId).getDrawable().apply {
                cornerRadius = 8.dp.toFloat()
            }
        }

        cityName.text = cityWithWeather.city.cityName
        weatherName.text = "${cityWithWeather.weather?.todayWeather?.weatherName} ${cityWithWeather.weather?.todayWeather?.temp}℃"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        iconSort.autoMeasure()
        iconDelete.autoMeasure()
        cityName.measure(
            (measuredWidth - iconSort.measuredWidthWithMargins - iconDelete.measuredWidthWithMargins).toExactlyMeasureSpec(), weatherName.defaultHeightMeasureSpec(this)
        )
        weatherName.measure(
            (measuredWidth - iconSort.measuredWidthWithMargins - iconDelete.measuredWidthWithMargins).toExactlyMeasureSpec(), weatherName.defaultHeightMeasureSpec(this)
        )
        setMeasuredDimension(
            measuredWidth, cityName.measuredHeightWithMargins + weatherName.measuredHeightWithMargins + paddingTop + paddingBottom
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        iconSort.layout(
            paddingLeft + iconSort.marginLeft, iconSort.toVerticalCenter(this)
        )
        cityName.layout(
            iconSort.right + cityName.marginLeft + iconSort.marginRight, paddingTop + cityName.marginTop
        )
        weatherName.layout(
            iconSort.right + cityName.marginLeft + iconSort.marginRight, cityName.bottom + weatherName.marginTop
        )
        iconDelete.layout(
            paddingRight, iconDelete.toVerticalCenter(this), true
        )
    }


}
