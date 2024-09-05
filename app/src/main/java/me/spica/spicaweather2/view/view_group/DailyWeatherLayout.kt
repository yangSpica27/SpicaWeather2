package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.HomeCardType
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.dailyItem.DailyItemView
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

class DailyWeatherLayout(
    context: Context,
) : AViewGroup(context),
    SpicaWeatherCard {
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var allHeight = 0f
        children.forEach {
            it.autoMeasure()
            allHeight += it.measuredHeightWithMargins
        }
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(
                allHeight.toInt() + paddingTop + paddingBottom,
                heightMeasureSpec,
            ),
        )
    }

    private val titleTextView =
        AppCompatTextView(context).apply {
            text = "天级别天气"
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).also {
                    it.updateMargins(
                        left = 14.dp,
                        top = 12.dp,
                        bottom = 8.dp,
                    )
                }
            setTextAppearance(context, R.style.TextAppearance_Material3_TitleMedium)
            typeface = Typeface.DEFAULT_BOLD
        }

    init {
        setBackgroundResource(R.drawable.bg_card)

        layoutParams =
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                leftMargin = 15.dp
                rightMargin = 15.dp
            }
        setPadding(12.dp, 8.dp, 12.dp, 0)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int,
    ) {
        titleTextView.layout(
            paddingLeft,
            paddingTop,
        )
        var top = titleTextView.bottom + titleTextView.marginBottom
        children.forEach { item ->
            if (item == titleTextView) return@forEach
            item.layout(
                item.left,
                top,
                item.measuredWidthWithMargins,
                top + item.measuredHeightWithMargins,
            )
            top += item.measuredHeightWithMargins
        }
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = HomeCardType.DAY_WEATHER.code
    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

    override fun bindData(weather: Weather) {
        removeAllViews()
        if (weather.dailyWeather.isEmpty()) return
        var minMinTemp = weather.dailyWeather[0].minTemp
        var maxMaxTemp = weather.dailyWeather[0].maxTemp
        weather.dailyWeather.forEach {
            minMinTemp = minOf(minMinTemp, it.minTemp)
            maxMaxTemp = maxOf(maxMaxTemp, it.maxTemp)
        }
        val themeColor = weather.getWeatherType().getThemeColor()
        titleTextView.setTextColor(themeColor)
        addView(titleTextView)
        weather.dailyWeather.forEachIndexed { index, it ->
            val lp =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            val dailyItemView = DailyItemView(context)
            dailyItemView.layoutParams = lp
            addView(dailyItemView)
            if (index == 0) {
                dailyItemView.currentTemp = weather.todayWeather.temp
            }
            dailyItemView.setData(it, index == 0, maxMaxTemp, minMinTemp)
        }
        //  如果入场动画未执行过 则归位
        if (!hasInScreen.get()) {
            children.forEachIndexed { _, childView ->
                childView.animate().cancel()
                childView.alpha = 0f
                childView.translationY = -12.dp.toFloat()
            }
        }
    }

    override fun startEnterAnim() {
        super.startEnterAnim()
        enterAnim.doOnEnd {
            children.forEachIndexed { index, childView ->
                childView
                    .animate()
                    .withLayer()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(30L * index)
                    .setDuration(150 + 120L * index)
                    .start()
            }
        }
    }
}
