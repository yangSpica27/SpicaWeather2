package me.spica.spicaweather2.view.view_group

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.ui.main.ActivityMain
import me.spica.spicaweather2.ui.weather.MainCardAdapter
import me.spica.spicaweather2.view.BounceEdgeEffectFactory2
import me.spica.spicaweather2.view.recyclerView.RecyclerViewAtViewPager2
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import rikka.recyclerview.fixEdgeEffect

//class WeatherMainLayout(context: Context) : RecyclerViewAtViewPager2(context) {
//
//    private var mainCardAdapter: MainCardAdapter
//
//    init {
//        val activityMain = getActivityFromContext(context) as ActivityMain
//        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).apply {
//            initialPrefetchItemCount = HomeCardType.values().size
//        }
//        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        context
//            .dividerBuilder()
//            .colorRes(android.R.color.transparent)
//            .size(8.dp.toInt())
//            .showFirstDivider()
//            .showLastDivider()
//            .build()
//            .addTo(this)
//
//        edgeEffectFactory = BounceEdgeEffectFactory2()
//
//        mainCardAdapter = MainCardAdapter(
//            this
//        )
//
//        mainCardAdapter.setItems(
//            HomeCardType.values().toMutableList()
//        )
//
//        adapter = mainCardAdapter
//
//        fixEdgeEffect()
//
//        setPullDownListener(object : PullDownListener {
//
//            override fun onPullDown(downY: Float) {
//            }
//
//            override fun onPullUp(downY: Float) {
//            }
//        })
//
//
//
//        addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                // 屏幕外不做检测
//                if (activityMain.currentCurrentCity?.cityName != tag.toString()) {
//                    return
//                }
//                if (activityMain.currentCurrentCity?.cityName == tag.toString()) {
////                    activityMain.positionAndOffset = getPositionAndOffset()
////                    layoutManager?.findViewByPosition(activityMain.positionAndOffset.first)?.let {
////                        if (it is NowWeatherLayout) {
////                            activityMain.setBox2dBackground(it.nowWeatherInfoCard.getNowCardTop())
////                        }
////                    }
//                }
//                mainCardAdapter.onScroll()
//            }
//        })
//    }
//
//    private fun getPositionAndOffset(): Pair<Int, Int> {
//        var lastOffset = 0
//        var lastPosition = 0
//        val layoutManager = layoutManager as LinearLayoutManager
//        // 获取可视的第一个view
//        val topView = layoutManager.getChildAt(0)
//        if (topView != null) {
//            // 获取与该view的顶部的偏移量
//            lastOffset = topView.top
//            // 得到该View的数组位置
//            lastPosition = layoutManager.getPosition(topView)
//        }
//        return Pair(lastPosition, lastOffset)
//    }
//
//    fun initData(weather: Weather) {
//        mainCardAdapter.notifyData(weather)
//        tag = weather.cityName
//    }
//
//    fun checkEnterScreen() {
//        mainCardAdapter.onScroll()
//    }
//
//
//
//
//    private fun getActivityFromContext(context: Context?): Activity? {
//        if (context == null) {
//            return null
//        }
//        if (context is Activity) {
//            return context
//        }
//        if (context is Application || context is Service) {
//            return null
//        }
//        var c = context
//        while (c != null) {
//            if (c is ContextWrapper) {
//                c = c.baseContext
//                if (c is Activity) {
//                    return c
//                }
//            } else {
//                return null
//            }
//        }
//        return null
//    }
//}
