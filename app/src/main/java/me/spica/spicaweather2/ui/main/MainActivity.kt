package me.spica.spicaweather2.ui.main

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.base.BindingActivity
import me.spica.spicaweather2.databinding.ActivityMainBinding
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.weather_bg.NowWeatherView


class MainActivity : BindingActivity<ActivityMainBinding>() {


    private val anim = ValueAnimator.ofFloat(0f, 1f).setDuration(450)


    private var isExpand = false


    override fun initializer() {
        viewBinding.weatherLayout.weatherBackground.bgColor = ContextCompat.getColor(this, R.color.light_blue_600)
        viewBinding.weatherLayout.weatherBackground.currentWeatherAnimType = NowWeatherView.WeatherAnimType.RAIN
        viewBinding.btnStart.setOnClickListener {
            anim.removeAllListeners()

            anim.addUpdateListener {
                 viewBinding.weatherLayout.weatherBackground.alpha = it.animatedValue as Float
                setExpandProgress(it.animatedValue as Float)
            }

            anim.doOnStart {
                 viewBinding.weatherLayout.weatherBackground.pauseWeatherAnim()
            }

            anim.doOnEnd {
                 viewBinding.weatherLayout.weatherBackground.resumeWeatherAnim()
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

        viewBinding.cardView.radius = 12.dp+ progress * 24.dp
        viewBinding.cardView.updateLayoutParams<MarginLayoutParams> {
            width = currentWidth.toInt()
            height = currentHeight.toInt()
            updateMargins(left = 0, top = 0, right = 0, bottom = marginBottomPixel.toInt())
        }

        viewBinding.cardView.setCardBackgroundColor(
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