package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.LifeIndexBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

class TipsLayout(
    context: Context,
) : AViewGroup(context),
    SpicaWeatherCard {
    // 运动指数
    private val sptItem = TipsItemLayout(context)

    // 穿衣指数
    private val clothesItem =
        TipsItemLayout(context).apply {
            layoutParams =
                MarginLayoutParams(
                    MarginLayoutParams.WRAP_CONTENT,
                    MarginLayoutParams.WRAP_CONTENT,
                ).apply {
                    leftMargin = 12.dp
                }
        }

    // 标题文字
    private val titleText =
        AppCompatTextView(context).apply {
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
            setTextAppearance(context, R.style.TextAppearance_Material3_TitleMedium)
            typeface = Typeface.DEFAULT_BOLD
            text = "生活指数"
        }

    // 空气指数
    private val airItem =
        TipsItemLayout(context).apply {
            layoutParams =
                MarginLayoutParams(
                    MarginLayoutParams.WRAP_CONTENT,
                    MarginLayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
        }

    // 洗车指数
    private val carItem =
        TipsItemLayout(context).apply {
            layoutParams =
                MarginLayoutParams(
                    MarginLayoutParams.WRAP_CONTENT,
                    MarginLayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
        }

    init {
        setPadding(
            14.dp,
            12.dp,
            14.dp,
            12.dp,
        )
        setBackgroundResource(R.drawable.bg_card)

        isFocusable = false
        isClickable = false
        val lp =
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                leftMargin = 14.dp
                rightMargin = 14.dp
            }
        layoutParams = lp
        addView(sptItem)
        addView(clothesItem)
        addView(airItem)
        addView(carItem)
        addView(titleText)
    }

    override fun bindData(weather: Weather) {
        val themeColor = weather.getWeatherType().getThemeColor()
        titleText.setTextColor(themeColor)
        val spt =
            weather.lifeIndexes.firstOrNull {
                it.type == LifeIndexBean.SPT
            }
        sptItem.setData("运动指数", spt?.category ?: "暂无数据")
        val clothes =
            weather.lifeIndexes.firstOrNull {
                it.type == LifeIndexBean.CLOTHES
            }
        clothesItem.setData("穿衣指数", clothes?.category ?: "暂无数据")
        val car =
            weather.lifeIndexes.firstOrNull {
                it.type == LifeIndexBean.CAR
            }
        carItem.setData("洗车指数", car?.category ?: "暂无数据")
        val air =
            weather.lifeIndexes.firstOrNull {
                it.type == LifeIndexBean.AIR
            }
        airItem.setData("空气指数", air?.category ?: "暂无数据")
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        children.forEach { childView ->
            childView.measure(
                MeasureSpec.makeMeasureSpec(
                    (measuredWidth - paddingLeft - paddingRight - 12.dp) / 2,
                    MeasureSpec.EXACTLY,
                ),
                childView.defaultHeightMeasureSpec(this@TipsLayout),
            )
        }
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(
                (sptItem.measuredHeightWithMargins + airItem.measuredHeightWithMargins + titleText.measuredHeightWithMargins) +
                    paddingTop + paddingBottom,
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
        // Title
        titleText.layout(paddingLeft, paddingTop)
        // SPT
        sptItem.layout(paddingLeft, paddingTop + titleText.bottom + titleText.marginBottom)
        // Clothes
        clothesItem.layout(sptItem.right + clothesItem.marginLeft, sptItem.top)
        // Air
        airItem.layout(paddingLeft, sptItem.bottom + airItem.marginTop)
        // Car
        carItem.layout(clothesItem.left, clothesItem.bottom + carItem.marginTop)
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.TIPS.code

    override var hasInScreen: AtomicBoolean = AtomicBoolean(false)
}
