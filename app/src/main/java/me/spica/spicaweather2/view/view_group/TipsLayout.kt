package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.marginTop
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.LifeIndexBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class TipsLayout(context: Context) : AViewGroup(context), SpicaWeatherCard {

    // SPT
    private val sptIcon = createIconView()

    private val sptTitle = createTitleView()

    private val sptDesc = createDescView()

    // CLOTHES
    private val clothesIcon = createIconView()

    private val clothesTitle = createTitleView()

    private val clothesDesc = createDescView()

    // AIR
    private val airIcon = createIconView()

    private val airTitle = createTitleView()

    private val airDesc = createDescView()

    // CAR
    private val carIcon = createIconView()

    private val carTitle = createTitleView()

    private val carDesc = createDescView()

    init {
        setPadding(
            14.dp,
            12.dp,
            14.dp,
            12.dp
        )
        setBackgroundResource(R.drawable.bg_card)
        isFocusable = false
        isClickable = false
        sptIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_spt))
        sptTitle.text = "运动指数"
        airIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_air_index))
        airTitle.text = "空气指数"
        clothesIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clothes))
        clothesTitle.text = "穿衣指数"
        carIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clean_car))
        carTitle.text = "洗车指数"
        val lp = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 14.dp
            rightMargin = 14.dp
        }
        layoutParams = lp
    }

    override fun bindData(weather: Weather) {
        val spt = weather.lifeIndexes.firstOrNull {
            it.type == LifeIndexBean.SPT
        }
        sptDesc.text = spt?.category ?: "暂无数据"
        val clothes = weather.lifeIndexes.firstOrNull {
            it.type == LifeIndexBean.CLOTHES
        }
        clothesDesc.text = clothes?.category ?: "暂无数据"
        val car = weather.lifeIndexes.firstOrNull {
            it.type == LifeIndexBean.CAR
        }
        carDesc.text = car?.category ?: "暂无数据"
        val air = weather.lifeIndexes.firstOrNull {
            it.type == LifeIndexBean.AIR
        }
        airDesc.text = air?.category ?: "暂无数据"
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun createIconView(): ImageView {
        val appCompatImageView = AppCompatImageView(context)
        appCompatImageView.layoutParams =
            LayoutParams(
                35.dp,
                35.dp
            ).apply {
                topMargin = 12.dp
            }
        addView(appCompatImageView)
        return appCompatImageView
    }

    private fun createTitleView(): TextView {
        val textView = AppCompatTextView(context)
        textView.layoutParams =
            LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                this.updateMargins(top = 12.dp)
            }
        textView.setTextAppearance(R.style.TextAppearance_Material3_TitleMedium)
        textView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
        addView(textView)
        return textView
    }

    private fun createDescView(): TextView {
        val textView = AppCompatTextView(context)
        textView.layoutParams =
            LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                this.updateMargins(top = 8.dp)
            }
        textView.setTextAppearance(R.style.TextAppearance_Material3_TitleSmall)
        textView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimaryLight))
        addView(textView)
        return textView
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        children.forEach { childView ->
            childView.autoMeasure()
        }
        setMeasuredDimension(
            measuredWidth,
            (
                carIcon.measuredHeightWithMargins +
                    carTitle.measuredHeightWithMargins +
                    carDesc.measuredHeightWithMargins
                ) * 2 +
                paddingTop +
                paddingBottom
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        // SPT
        sptIcon.layout(
            measuredWidth / 6 - sptIcon.width / 2,
            paddingTop + sptIcon.marginTop
        )
        sptTitle.layout(
            sptTitle.toViewHorizontalCenter(sptIcon),
            sptIcon.bottom + sptTitle.marginTop
        )
        sptDesc.layout(
            sptDesc.toViewHorizontalCenter(sptIcon),
            sptTitle.bottom + sptDesc.marginTop
        )
        // clothes
        clothesIcon.layout(
            clothesIcon.toHorizontalCenter(this),
            paddingTop + clothesIcon.marginTop
        )
        clothesTitle.layout(
            clothesTitle.toHorizontalCenter(this),
            clothesIcon.bottom + clothesTitle.marginTop
        )
        clothesDesc.layout(
            clothesDesc.toHorizontalCenter(this),
            clothesTitle.bottom + clothesTitle.marginTop
        )
        // air
        airIcon.layout(
            measuredWidth / 6 - airIcon.width / 2,
            paddingTop + airIcon.marginTop,
            true
        )
        airTitle.layout(
            airTitle.toViewHorizontalCenter(airIcon),
            airIcon.bottom + airTitle.marginTop
        )
        airDesc.layout(
            airDesc.toViewHorizontalCenter(airIcon),
            airTitle.bottom + airDesc.marginTop
        )

        // car
        carIcon.layout(
            carIcon.toViewHorizontalCenter(sptIcon),
            sptDesc.bottom + carIcon.marginTop,
        )
        carTitle.layout(
            carTitle.toViewHorizontalCenter(sptIcon),
            carIcon.bottom + carTitle.marginTop,
        )
        carDesc.layout(
            carDesc.toViewHorizontalCenter(sptIcon),
            carTitle.bottom + carDesc.marginTop,
        )
    }

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()

    override var index: Int = HomeCardType.TIPS.code

    override var hasInScreen: Boolean = false
}
