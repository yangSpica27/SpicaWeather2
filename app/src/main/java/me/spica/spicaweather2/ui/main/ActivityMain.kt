package me.spica.spicaweather2.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.children
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.tools.MessageEvent
import me.spica.spicaweather2.tools.MessageType
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.tools.startActivityWithAnimation
import me.spica.spicaweather2.ui.manager_city.ActivityManagerCity
import me.spica.spicaweather2.ui.test.TestActivity
import me.spica.spicaweather2.view.Manger2HomeView
import me.spica.spicaweather2.view.view_group.ActivityMainLayout
import me.spica.spicaweather2.view.view_group.WeatherMainLayout
import me.spica.spicaweather2.view.weather_bg.NowWeatherView
import me.spica.spicaweather2.work.DataSyncWorker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rikka.material.app.MaterialActivity

@AndroidEntryPoint
class ActivityMain : MaterialActivity() {

    companion object {
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

    var positionAndOffset = Pair(0, 0)
        set(value) {
            if (value.first == 0) {
                val progress = -value.second * 1f / (resources.displayMetrics.heightPixels / 3f)
                layout.mainTitleLayout.setBackgroundWhiteColor(progress)
                WindowCompat.getInsetsController(
                    window,
                    window.decorView
                ).isAppearanceLightStatusBars = progress > 0.5
            } else {
                layout.mainTitleLayout.setBackgroundWhiteColor(1f)
                WindowCompat.getInsetsController(
                    window,
                    window.decorView
                ).isAppearanceLightStatusBars = true
            }
            field = value
        }

    var currentCurrentCity: CityBean? = null

    private var data = listOf<CityWithWeather>()

    private val manger2HomeView by lazy {
        Manger2HomeView(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.tag) {
            MessageType.Get2MainActivityAnim.tag -> {
                layout.viewPager2.setCurrentItem(event.extra as Int, false)
                manger2HomeView.invalidate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (manger2HomeView.isAttached) {
            doOnMainThreadIdle({
                manger2HomeView.startAnim()
            })
        }
    }

    private fun initializer() {
        layout.mainTitleLayout.plusBtn.setOnClickListener {
            enterManagerCity()
        }

        layout.mainTitleLayout.titleTextView.setOnClickListener {
            startActivityWithAnimation<TestActivity> { }
        }
//        layout.viewPager2.hide()
        layout.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        layout.viewPager2.adapter = mainPagerAdapter
        layout.viewPager2.isUserInputEnabled = true
        startService(Intent(this, DataSyncWorker::class.java))
        layout.mainTitleLayout.dotIndicator.setViewPager2(viewPager2 = layout.viewPager2)
        lifecycleScope.launch {
            viewModel.allCityWithWeather.collectLatest {
                mainPagerAdapter.updateCities(it)
                data = it
                updateTitle(0)
            }
        }

        layout.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTitle(position)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                updateOtherPageScroller()
            }
        })
    }

    private fun enterManagerCity() {
        manger2HomeView.attachToRootView()
        lifecycleScope.launch(Dispatchers.Default) {
            if (screenBitmap?.isRecycled == false) {
                screenBitmap?.recycle()
                screenBitmap = null
            }
            layout.weatherBackgroundSurfaceView.getScreenCopy(
                window.decorView.drawToBitmap()
            ) {
                screenBitmap = it
                startActivityWithAnimation<ActivityManagerCity> {
                    putExtra(ActivityManagerCity.ARG_CITY_NAME, currentCurrentCity?.cityName)
                }
            }
        }
    }

    private fun updateOtherPageScroller() {
        (layout.viewPager2.children.first() as RecyclerView)
            .children.forEach {
                if (it is WeatherMainLayout) {
                    (it.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        positionAndOffset.first,
                        positionAndOffset.second
                    )
                }
            }
    }

    private fun updateTitle(position: Int) {
        try {
            val currentWeather = data[position].weather
            val currentCity = data[position].city
            layout.mainTitleLayout.titleTextView.text = currentCity.cityName
            currentCurrentCity = currentCity
            WeatherCodeUtils.getWeatherCode(currentWeather?.todayWeather?.iconId ?: 100).let {
                layout.weatherBackgroundSurfaceView.currentWeatherAnimType = NowWeatherView.WeatherAnimType.RAIN
                layout.weatherBackgroundSurfaceView.bgColor = it.getThemeColor()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio)
        val r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio)
        val g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio)
        val b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio)
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }


    fun setBox2dBackground(y:Int){
        layout.weatherBackgroundSurfaceView.setBackgroundY(y)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
