package me.spica.spicaweather2.view.view_group

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.ui.main.ActivityMain
import me.spica.spicaweather2.view.scroller_view.ScrollViewAtViewPager
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

class WeatherMainLayout2 : ScrollViewAtViewPager {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private val items = HomeCardType.values().toMutableList()

    private val contentView = LinearLayout(context).apply {
        layoutParams = MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = 40.dp.toInt()
        }
        orientation = LinearLayout.VERTICAL
    }

    init {
        val activityMain = getActivityFromContext(context) as ActivityMain
        layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // 载入item数据
        items.forEachIndexed { index, item ->
            val itemView = item.getViewType(context)
            itemView.tag = item.name
            itemView.layoutParams = MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = if (index == 0) 100.dp.toInt() else 8.dp.toInt()
                leftMargin = if (index == 0) 0 else 14.dp.toInt()
                rightMargin = if (index == 0) 0 else 14.dp.toInt()
            }
            if (itemView is SpicaWeatherCard) {
                itemView.resetAnim()
                itemView.doOnPreDraw {
                    itemView.requestLayout()
                }
            }
            contentView.addView(itemView)
        }
        addView(contentView)
        setOnScrollChangeListener { _, _, _, _, _ ->
            // 屏幕外不做检测
            if (activityMain.currentCurrentCity?.cityName != tag.toString()) {
                return@setOnScrollChangeListener
            }
            if (activityMain.currentCurrentCity?.cityName == tag.toString()) {
                activityMain.listScrollerY = scrollY
                contentView.children.firstOrNull().let {
                    if (it is NowWeatherLayout) {
                        activityMain.setBox2dBackground(it.nowWeatherInfoCard.getNowCardTop())
                    }
                }
            }
            checkItemInScreen()
        }

        checkItemInScreen()
        isVerticalScrollBarEnabled = false
    }


    // 复用的rect 防止内存抖动
    private val itemVisibleRect = Rect()


    fun initData(weather: Weather) {
        contentView.children.forEach { view ->
            if (view is SpicaWeatherCard) {
                view.bindData(weather)
            }
        }
        tag = weather.cityName
        checkItemInScreen()
    }

    // 检查是否进入屏幕进行动画
    fun checkItemInScreen() {
        contentView.children.forEach { itemView ->
            if (itemView is SpicaWeatherCard) {
                if (!itemView.hasInScreen) {
                    val isVisible = itemView.getGlobalVisibleRect(itemVisibleRect)
                    itemView.checkEnterScreen(isVisible && itemVisibleRect.bottom - itemVisibleRect.top >= itemView.height / 10f)
                }
            }
        }
    }

    private fun getActivityFromContext(context: Context?): Activity? {
        if (context == null) {
            return null
        }
        if (context is Activity) {
            return context
        }
        if (context is Application || context is Service) {
            return null
        }
        var c = context
        while (c != null) {
            if (c is ContextWrapper) {
                c = c.baseContext
                if (c is Activity) {
                    return c
                }
            } else {
                return null
            }
        }
        return null
    }

}