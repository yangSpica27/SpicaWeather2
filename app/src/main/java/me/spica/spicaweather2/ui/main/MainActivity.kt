package me.spica.spicaweather2.ui.main

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.spica.spicaweather2.R
import me.spica.spicaweather2.base.BindingActivity
import me.spica.spicaweather2.databinding.ActivityMainBinding
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.view.weather_bg.NowWeatherView
import me.spica.spicaweather2.work.DataSyncWorker
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>() {

    private val viewModel: MainViewModel by viewModels()

    private val anim = ValueAnimator.ofFloat(0f, 1f).setDuration(450)

    private val mainPagerAdapter by lazy {
        MainPagerAdapter(this)
    }


    private var isExpand = false


    override fun initializer() {
        viewBinding.weatherLayout.viewPager.adapter = mainPagerAdapter

        lifecycleScope.launch {
            viewModel.allCityFlow.collectLatest {
                Timber.tag("获取到城市").e("${it}个")
                mainPagerAdapter.diffUtil.submitList(it)
            }
        }

        mainPagerAdapter.diffUtil.submitList(
            listOf(
                CityBean(cityName = "阿坝", sortName = "aba", lat = "31.93", lon = "102.72", isSelected = true),
            )
        )

        startService(Intent(this, DataSyncWorker::class.java))

        viewBinding.weatherBackground.bgColor = ContextCompat.getColor(this, R.color.light_blue_600)
        viewBinding.weatherBackground.currentWeatherAnimType = NowWeatherView.WeatherAnimType.RAIN
        viewBinding.btnStart.setOnClickListener {
            anim.removeAllListeners()

            anim.addUpdateListener {
                viewBinding.weatherBackground.alpha = it.animatedValue as Float
                setExpandProgress(it.animatedValue as Float)
            }

            anim.doOnStart {
                viewBinding.weatherBackground.pauseWeatherAnim()
                viewBinding.weatherLayout.root.visibility = View.GONE
            }

            anim.doOnEnd {
                viewBinding.weatherBackground.resumeWeatherAnim()
                viewBinding.weatherLayout.root.visibility = View.VISIBLE
            }

            if (!isExpand) {
                anim.setFloatValues(anim.animatedValue as Float, 0f)
                anim.start()
            } else {
                anim.setFloatValues(anim.animatedValue as Float, 1f)
                anim.start()
            }

            isExpand = !isExpand
        }
    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivityMainBinding = ActivityMainBinding.inflate(inflater)

    private fun setExpandProgress(progress: Float) {

        val fullWidth = resources.displayMetrics.widthPixels
        val fullHeight = resources.displayMetrics.heightPixels

        val minWidth = fullWidth / 8f * 7
        val minHeight = fullHeight / 8f

        val currentWidth = (fullWidth - minWidth) * progress + minWidth
        val currentHeight = (fullHeight - minHeight) * progress * 1.1 + minHeight

        val marginBottomPixel = fullHeight / 4f - fullHeight / 4f * progress


        viewBinding.cardView.updateLayoutParams<MarginLayoutParams> {
            width = currentWidth.toInt()
            height = currentHeight.toInt()
            updateMargins(left = 0, top = 0, right = 0, bottom = marginBottomPixel.toInt())
        }

        viewBinding.cardView.setBackgroundColor(
            blendColors(
                Color.WHITE,
                Color.TRANSPARENT,
                progress
            )
        )

        viewBinding.cardView.requestLayout()

    }


    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio)
        val r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio)
        val g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio)
        val b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio)
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

}