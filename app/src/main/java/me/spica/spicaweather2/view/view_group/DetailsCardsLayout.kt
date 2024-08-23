package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.marginTop
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.common.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

// 描述卡片组
class DetailsCardsLayout(
    context: Context,
) : AViewGroup(context),
    SpicaWeatherCard {
    private val uvDescCard = DescCardViewLayout(context).apply {
        setAnimDelay(125)
    }

    private val humidityDescCard = DescCardViewLayout(context).apply {
        setAnimDelay(275)
    }


    private val feelTempDescCard =
        DescCardViewLayout(context).apply {
            layoutParams =
                MarginLayoutParams(
                    MarginLayoutParams.MATCH_PARENT,
                    MarginLayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
        }.apply {
            setAnimDelay(525)
        }


    private val sunRiseDescCard =
        DescCardViewLayout(context).apply {
            layoutParams =
                MarginLayoutParams(
                    MarginLayoutParams.MATCH_PARENT,
                    MarginLayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
        }.apply {
            setAnimDelay(350)
        }


    init {
        addView(uvDescCard)
        addView(humidityDescCard)
        addView(feelTempDescCard)
        addView(sunRiseDescCard)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        uvDescCard.measure(
            (measuredWidth / 2 - 6.dp).toExactlyMeasureSpec(),
            uvDescCard.defaultHeightMeasureSpec(this),
        )
        humidityDescCard.measure(
            (measuredWidth / 2 - 6.dp).toExactlyMeasureSpec(),
            humidityDescCard.defaultHeightMeasureSpec(this),
        )
        feelTempDescCard.measure(
            (measuredWidth / 2 - 6.dp).toExactlyMeasureSpec(),
            feelTempDescCard.defaultHeightMeasureSpec(this),
        )
        sunRiseDescCard.measure(
            (measuredWidth / 2 - 6.dp).toExactlyMeasureSpec(),
            sunRiseDescCard.defaultHeightMeasureSpec(this),
        )
        setMeasuredDimension(
            measuredWidth,
            uvDescCard.measuredHeightWithMargins + feelTempDescCard.measuredHeightWithMargins,
        )
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int,
    ) {
        uvDescCard.layout(0, 0)
        humidityDescCard.layout(uvDescCard.right + 12.dp, 0)
        feelTempDescCard.layout(0, humidityDescCard.bottom + feelTempDescCard.marginTop)
        sunRiseDescCard.layout(
            feelTempDescCard.right + 12.dp,
            humidityDescCard.bottom + sunRiseDescCard.marginTop,
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.DETAILS.code

    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

    override fun startEnterAnim() {
        super.startEnterAnim()

        uvDescCard
            .animate()
            .alpha(1f)
            .setStartDelay(250)
            .setDuration(250)
            .start()

        humidityDescCard
            .animate()
            .alpha(1f)
            .setStartDelay(50)
            .setDuration(550)
            .start()
        feelTempDescCard
            .animate()
            .alpha(1f)
            .setStartDelay(150)
            .setDuration(350)
            .start()
        sunRiseDescCard
            .animate()
            .alpha(1f)
            .setDuration(850)
            .setInterpolator(AnimationUtils.loadInterpolator(context, android.R.anim.decelerate_interpolator))
            .start()

        uvDescCard.doShowEnterAnimator()
        humidityDescCard.doShowEnterAnimator()
        feelTempDescCard.doShowEnterAnimator()
        sunRiseDescCard.doShowEnterAnimator()
    }

    override fun resetAnim() {
        uvDescCard.alpha = 0f
        humidityDescCard.alpha = 0f
        feelTempDescCard.alpha = 0f
        sunRiseDescCard.alpha = 0f
        uvDescCard.resetAnim()
        humidityDescCard.resetAnim()
        feelTempDescCard.resetAnim()
        sunRiseDescCard.resetAnim()
    }

    override fun bindData(weather: Weather) {
        weather.todayWeather.let {
            feelTempDescCard.setShowData(
                R.drawable.ic_plastic_surgery,
                "体感温度",
                "${it.feelTemp}摄氏度",
                it.getFeelTempDescription(),
                (it.feelTemp / 40.0)
                    .coerceAtMost(1.0)
                    .coerceAtLeast(0.0),
                3,
            )
            humidityDescCard.setShowData(
                R.drawable.ic_aquarius,
                "湿度",
                "${it.water}%",
                it.getFeelTempDescription(),
                (it.water / 100.0)
                    .coerceAtMost(1.0)
                    .coerceAtLeast(0.0),
                2,
            )
        }
        weather.dailyWeather.firstOrNull()?.let {
            uvDescCard.setShowData(
                R.drawable.ic_texture,
                "紫外线指数",
                it.getUVLevelDescription(),
                it.getUVDescription(),
                (((it.uv.toIntOrNull() ?: 0) / 12.0).coerceAtMost(1.0).coerceAtLeast(0.0)),
                1,
            )
            if (weather.dailyWeather.isEmpty()) return
            val startTime = decodeTime(weather.dailyWeather[0].sunriseDate())
            val endTime = decodeTime(weather.dailyWeather[0].sunsetDate())
            val currentTime = decodeTime(Date())
            sunRiseDescCard.setShowData(
                R.drawable.ic_round_mask,
                "日出日落",
                "",
                "日出：${it.sunriseDate}\n日落：${it.sunsetDate}",
                ((currentTime - startTime).toDouble() / (endTime - startTime))
                    .coerceAtMost(1.0)
                    .coerceAtLeast(0.0),
                4,
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun decodeTime(time: Date): Int = time.hours * 60 + time.minutes
}
