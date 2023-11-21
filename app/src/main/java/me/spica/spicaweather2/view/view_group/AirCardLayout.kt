package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.AirCircleProgressView
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class AirCardLayout(context: Context) : AViewGroup(context), SpicaWeatherCard {


    // 标题文字
    private val titleText = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.updateMargins(
                left = 14.dp, top = 12.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_TitleMedium)
        setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
        typeface = Typeface.DEFAULT_BOLD
    }


    // 进度条
    private val airCircleProgressView = AirCircleProgressView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 14.dp, top = 12.dp
            )
        }
    }

    private val tvC0Title = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 12.dp, top = 12.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelMedium)
    }
    private val tvC0Value = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                right = 14.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_BodySmall)
    }

    private val tvSo2Title = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 12.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelMedium)
    }
    private val tvSo2Value = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                right = 14.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_BodySmall)
    }

    private val tvNo2Title = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 12.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelMedium)
    }
    private val tvNo2Value = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                right = 14.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_BodySmall)
    }

    private val tvPm25Title = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                left = 12.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelMedium)
    }
    private val tvPm25Value = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.updateMargins(
                right = 14.dp
            )
        }
        setTextAppearance(context, R.style.TextAppearance_Material3_BodySmall)
    }

    init {
        isFocusable = false
        isClickable = false
        setBackgroundResource(R.drawable.bg_card)
        setPadding(0, 0, 0, 12.dp)
        addView(titleText)
        addView(airCircleProgressView)
        addView(tvC0Title)
        addView(tvC0Value)
        addView(tvNo2Title)
        addView(tvNo2Value)
        addView(tvSo2Title)
        addView(tvSo2Value)
        addView(tvPm25Title)
        addView(tvPm25Value)
        val lp = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
            it.updateMargins(left = 14.dp, right = 14.dp)
        }
        layoutParams = lp
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        titleText.autoMeasure()
        airCircleProgressView.measure(
            (measuredWidth / 2 - airCircleProgressView.marginLeft).toExactlyMeasureSpec(), airCircleProgressView.defaultHeightMeasureSpec(this)
        )

        tvC0Title.autoMeasure()
        tvC0Value.autoMeasure()
        tvNo2Title.autoMeasure()
        tvNo2Value.autoMeasure()
        tvSo2Title.autoMeasure()
        tvSo2Value.autoMeasure()
        tvPm25Title.autoMeasure()
        tvPm25Value.autoMeasure()
        setMeasuredDimension(
            measuredWidth, Math.max(
                airCircleProgressView.measuredHeightWithMargins + titleText.measuredHeightWithMargins, tvNo2Title.measuredHeightWithMargins + tvC0Title.measuredHeightWithMargins + tvPm25Title.measuredHeightWithMargins + tvSo2Title.measuredHeightWithMargins + titleText.measuredHeightWithMargins + paddingBottom
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        titleText.layout(titleText.marginLeft, titleText.marginTop)

        airCircleProgressView.layout(
            airCircleProgressView.marginLeft, titleText.bottom + airCircleProgressView.marginTop
        )

        val titleHeight =
            tvC0Title.height +
                tvPm25Title.height +
                tvSo2Title.height +
                titleText.height

        val progressHeight = airCircleProgressView.height

        val itemSpacer = Math.max(0, progressHeight - titleHeight) / 4


        tvC0Title.layout(
            width / 2 + tvC0Title.marginLeft,
            airCircleProgressView.top + tvC0Title.marginTop
        )

        tvC0Value.layout(
            tvC0Value.marginRight,
            tvC0Title.bottom - tvC0Value.height, true
        )


        tvSo2Title.layout(
            width / 2 + tvSo2Title.marginLeft,
            tvC0Title.bottom + itemSpacer
        )

        tvSo2Value.layout(
            tvSo2Value.marginRight, tvSo2Title.bottom - tvSo2Value.height, true
        )

        tvNo2Title.layout(
            width / 2 + tvNo2Title.marginLeft,
            tvSo2Title.bottom + itemSpacer
        )

        tvNo2Value.layout(
            tvNo2Value.marginRight, tvNo2Title.bottom - tvNo2Value.height, true
        )

        tvPm25Title.layout(
            width / 2 + tvPm25Title.marginLeft,
            tvNo2Title.bottom + itemSpacer
        )

        tvPm25Value.layout(
            tvPm25Value.marginRight,
            tvPm25Title.bottom - tvPm25Value.height, true
        )


    }


    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 2
    override var hasInScreen: Boolean = false

    override fun bindData(weather: Weather) {
        val themeColor = weather.getWeatherType().getThemeColor()
        airCircleProgressView.bindProgress(weather.air.aqi, weather.air.category)
        titleText.setTextColor(themeColor)
        titleText.text = "空气质量"
        tvC0Title.text = "二氧化碳"
        tvC0Value.text = "${weather.air.co}微克/m³"
        tvNo2Title.text = "二氧化氮"
        tvNo2Value.text = "${weather.air.no2}微克/m³"
        tvPm25Title.text = "PM2.5"
        tvPm25Value.text = "${weather.air.pm2p5}微克/m³"
        tvSo2Title.text = "二氧化硫"
        tvSo2Value.text = "${weather.air.so2}微克/m³"
        airCircleProgressView.postInvalidate()
    }


}