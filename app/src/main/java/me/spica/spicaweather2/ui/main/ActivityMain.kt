package me.spica.spicaweather2.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.core.view.children
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.spica.spicaweather2.common.WeatherType
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.common.getWeatherAnimType
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.tools.MessageEvent
import me.spica.spicaweather2.tools.MessageType
import me.spica.spicaweather2.tools.startActivityWithAnimation
import me.spica.spicaweather2.ui.manager_city.ActivityManagerCity
import me.spica.spicaweather2.view.Manager2HomeView
import me.spica.spicaweather2.view.view_group.ActivityMainLayout
import me.spica.spicaweather2.view.view_group.WeatherMainLayout2
import me.spica.spicaweather2.work.DataSyncWorker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rikka.material.app.MaterialActivity
import timber.log.Timber

/**
 * 主页面
 */
@AndroidEntryPoint
class ActivityMain : MaterialActivity() {
    companion object {
        // 当前页面的 截图
        var screenBitmap: Bitmap? = null
        var currentThemeColor: Int = Color.parseColor("#4297e7")
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

    // 用于播放动画的覆盖物
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
            manager2HomeView.endAction = {
                layout.currentWeatherLayout.dotIndicator.refreshDots()
                updateOtherPageScroller()
            }
            manager2HomeView.startAnim()
        }
    }

    private fun initializer() {
        handleBack()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        layout.mainTitleLayout.plusBtn.setOnClickListener {
            enterManagerCity()
        }

        layout.mainTitleLayout.titleTextView.setOnClickListener {
            AlertDialog
                .Builder(this)
                .setTitle("选择天气动画类型")
                .setItems(
                    arrayOf(
                        "晴天",
                        "多云",
                        "雨天",
                        "霾",
                        "沙尘暴",
                    ),
                ) { _, which ->
                    val type =
                        when (which) {
                            0 -> WeatherType.WEATHER_SUNNY
                            1 -> WeatherType.WEATHER_CLOUDY
                            2 -> WeatherType.WEATHER_RAINY
                            3 -> WeatherType.WEATHER_FOG
                            else -> WeatherType.WEATHER_SANDSTORM
                        }
                    with(layout.weatherBackgroundSurfaceView) {
                        bgColor = type.getThemeColor()
                        currentWeatherAnimType = type.getWeatherAnimType()
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
        layout.currentWeatherLayout.dotIndicator.attachTo(viewPager2 = layout.viewPager2)
        lifecycleScope.launch {
            viewModel.allCityWithWeather.collectLatest {
                mainPagerAdapter.updateCities(it)
                data = it
                updateTitleAndAnim(layout.viewPager2.currentItem)
            }
        }

        layout.viewPager2.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 更新标题
                    viewModel.setCurrentPagerIndex(position)
                    layout.currentWeatherLayout.dotIndicator.refreshDots()
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    // 同步当前页面的滚动
                    updateOtherPageScroller()
                }
            },
        )

        lifecycleScope.launch {
            viewModel.currentPagerIndex.collectLatest { position ->
                updateTitleAndAnim(position)
            }
        }
    }

    // 返回拦截
    private fun handleBack() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog
                        .Builder(this@ActivityMain)
                        .setTitle("退出应用")
                        .setMessage("是否退出应用")
                        .setPositiveButton("退出") { _, _ ->
                            finish()
                        }.setNegativeButton("取消") { _, _ -> }
                        .show()
                }
            },
        )
    }

    // 进入城市管理页面
    private fun enterManagerCity() {
        manager2HomeView.attachToRootView()
        lifecycleScope.launch(Dispatchers.Default) {
            if (screenBitmap?.isRecycled == false) {
                screenBitmap?.recycle()
                screenBitmap = null
            }
            val count1 = System.currentTimeMillis()
            val bgBitmap: Bitmap =
                try {
                    window.decorView.drawToBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Bitmap.createBitmap(
                        window.decorView.width,
                        window.decorView.height,
                        Bitmap.Config.ARGB_8888,
                    )
                }

            layout.weatherBackgroundSurfaceView.getScreenCopy(
                bgBitmap,
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
        layout.currentWeatherLayout.alpha =
            (1 - scrollY / 1350f)
                .coerceAtLeast(0f)
                .coerceAtMost(1f)
        layout.currentWeatherLayout.scaleX =
            (1 - scrollY / 3000f)
                .coerceAtLeast(.85f)
                .coerceAtMost(1f)
        layout.currentWeatherLayout.scaleY =
            (1 - scrollY / 3000f)
                .coerceAtLeast(0.85f)
                .coerceAtMost(1f)
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
                    currentThemeColor = bgColor
                    currentWeatherAnimType = it.getWeatherAnimType()
                    layout.currentWeatherLayout.bindData(currentWeather)
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
