package me.spica.spicaweather2.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.doOnDetach
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.WeatherType
import me.spica.spicaweather2.common.getBackgroundBitmap
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.common.getWeatherAnimType
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.tools.MessageEvent
import me.spica.spicaweather2.tools.MessageType
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.tools.startActivityWithAnimation
import me.spica.spicaweather2.ui.manager_city.ActivityManagerCity
import me.spica.spicaweather2.view.Manager2HomeView
import me.spica.spicaweather2.view.view_group.ActivityMainLayout
import me.spica.spicaweather2.view.view_group.WeatherMainLayout2
import me.spica.spicaweather2.work.DataSyncWorker
import okhttp3.internal.connection.ConnectInterceptor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rikka.material.app.MaterialActivity
import timber.log.Timber
import kotlin.system.measureTimeMillis

/**
 * 主页面
 */
@AndroidEntryPoint
class ActivityMain : MaterialActivity() {

    companion object {
        // 当前页面的 截图
        var screenBitmap: Bitmap? = null
    }

    private val viewModel: MainViewModel by viewModels()

    private val mainPagerAdapter by lazy {
        MainViewAdapter()
    }

    private val layout by lazy {
        ActivityMainLayout(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        initializer()
        EventBus.getDefault().register(this)
    }


    // 用于同步滑动的变量
    var listScrollerY = 0
        set(value) {
            updateTitleBarUI(value)
            field = value
        }

    var currentCurrentCity: CityBean? = null

    private var data = listOf<CityWithWeather>()

    private val manager2HomeView by lazy {
        Manager2HomeView(this)
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.tag) {
            MessageType.Get2MainActivityAnim.tag -> {
                // 从管理城市页面返回进行动画 切换到用户选择的城市上去
                layout.viewPager2.setCurrentItem(event.extra as Int, false)
                manager2HomeView.invalidate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 从管理城市页面返回进行动画
        if (manager2HomeView.isAttached) {
            doOnMainThreadIdle({
                manager2HomeView.endAction = {
                    layout.mainTitleLayout.dotIndicator.refreshDots()
                    updateOtherPageScroller()
                }
                manager2HomeView.startAnim()
            })
        }
    }

    private fun initializer() {
//        handleBack()
        layout.mainTitleLayout.plusBtn.setOnClickListener {
            enterManagerCity()
        }

        layout.mainTitleLayout.titleTextView.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("选择天气动画类型").setItems(
                    arrayOf(
                        "晴天", "多云", "雨天", "霾", "雾天"
                    )
                ) { _, which ->
                    val type = when (which) {
                        0 -> WeatherType.WEATHER_SUNNY
                        1 -> WeatherType.WEATHER_CLOUDY
                        3 -> WeatherType.WEATHER_HAZE
                        4 -> WeatherType.WEATHER_FOG
                        else -> WeatherType.WEATHER_RAINY
                    }
                    with(layout.weatherBackgroundSurfaceView) {
                        bgColor = type.getThemeColor()
                        currentWeatherAnimType = type.getWeatherAnimType()
                        bgBitmap = type.getBackgroundBitmap(this@ActivityMain)
                    }
                }.show()
        }
        with(layout.viewPager2) {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 10
            adapter = mainPagerAdapter
            isUserInputEnabled = true
        }

        // 启动后台同步
        startService(Intent(this, DataSyncWorker::class.java))
        layout.mainTitleLayout.dotIndicator.attachTo(viewPager2 = layout.viewPager2)
        lifecycleScope.launch {
            viewModel.allCityWithWeather.collectLatest {
                mainPagerAdapter.updateCities(it)
                data = it
                updateTitleAndAnim(layout.viewPager2.currentItem)
            }
        }

        layout.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 更新标题
                viewModel.setCurrentPagerIndex(position)
                layout.mainTitleLayout.dotIndicator.refreshDots()
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                // 同步当前页面的滚动
                updateOtherPageScroller()
            }
        })

        lifecycleScope.launch {
            viewModel.currentPagerIndex.collectLatest { position ->
                updateTitleAndAnim(position)
            }
        }

    }

    // 进入城市管理页面
    private fun enterManagerCity() {
        manager2HomeView.attachToRootView()
        lifecycleScope.launch(Dispatchers.Default) {
            if (screenBitmap?.isRecycled == false) {
                val countRecycler = measureTimeMillis {
                    screenBitmap?.recycle()
                    screenBitmap = null
                }
                Timber.tag("销毁位图耗时").e("${countRecycler}ms")
            }
            val count1 = System.currentTimeMillis()
            val bgBitmap: Bitmap = try {
                window.decorView.drawToBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
                Bitmap.createBitmap(
                    window.decorView.width,
                    window.decorView.height,
                    Bitmap.Config.ARGB_8888
                )
            }

            layout.weatherBackgroundSurfaceView.getScreenCopy(
                bgBitmap
            ) {
                screenBitmap = it
                val count2 = System.currentTimeMillis()
                Timber.tag("获取位图耗时").e("${count2 - count1}ms")
                startActivityWithAnimation<ActivityManagerCity> {
                    putExtra(ActivityManagerCity.ARG_CITY_NAME, currentCurrentCity?.cityName)
                }
            }
        }
    }


    // 更新标题
    private fun updateTitleBarUI(scrollY: Int) {
        layout.mainTitleLayout.translationY = -scrollY * 1f
        layout.weatherBackgroundSurfaceView.setMScrollY(scrollY)
    }

    // 更新其他页面的滚动
    private fun updateOtherPageScroller() {
        (layout.viewPager2.children.first() as RecyclerView).children.forEachIndexed { index, view ->
            Timber.tag("滚动").e("index:$index")
            if (view is WeatherMainLayout2) {
                view.scrollTo(0, listScrollerY)
                view.updateBackgroundY()
                view.checkItemInScreen()
            }
        }
    }

    // 更新标题和动画
    private fun updateTitleAndAnim(position: Int) {
        if (data.isEmpty()) return
        try {
            val currentWeather = data[position].weather
            val currentCity = data[position].city
            layout.mainTitleLayout.titleTextView.text = currentCity.cityName
            currentCurrentCity = currentCity
            currentWeather?.getWeatherType()?.let {
                with(layout.weatherBackgroundSurfaceView) {
                    bgColor = it.getThemeColor()
                    currentWeatherAnimType = it.getWeatherAnimType()
                    bgBitmap = it.getBackgroundBitmap(this@ActivityMain)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 给引擎传入刚体进行碰撞计算
    fun setBox2dBackground(y: Int) {
        layout.weatherBackgroundSurfaceView.setBackgroundY(y)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
