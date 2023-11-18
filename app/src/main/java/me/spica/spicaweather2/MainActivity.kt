package me.spica.spicaweather2

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import me.spica.weather.view.WeatherBackgroundView


class MainActivity : AppCompatActivity() {


    private val weatherBackgroundView: WeatherBackgroundView by lazy {
        findViewById(R.id.weather_background)
    }

    private val titleTextView: androidx.appcompat.widget.AppCompatTextView by lazy {
        findViewById(R.id.title_text_view)
    }

    private val cardView: androidx.cardview.widget.CardView by lazy {
        findViewById(R.id.card_view)
    }


    private val anim = ValueAnimator.ofFloat(0f, 1f).setDuration(450)


    private var isExpand = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherBackgroundView.bgColor = ContextCompat.getColor(this, R.color.light_blue_600)
        weatherBackgroundView.currentWeatherAnimType = WeatherBackgroundView.WeatherAnimType.HAZE
        cardView.setOnClickListener {
            anim.removeAllListeners()

            anim.addUpdateListener {
                weatherBackgroundView.alpha = it.animatedValue as Float
                titleTextView.alpha = (1f - it.animatedValue as Float)
                setExpandProgress(it.animatedValue as Float)
            }

            anim.doOnStart {
                weatherBackgroundView.pauseWeatherAnim()
            }

            anim.doOnEnd {
                weatherBackgroundView.resumeWeatherAnim()
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

    private fun setExpandProgress(progress: Float) {

        val fullWidth = resources.displayMetrics.widthPixels
        val fullHeight = resources.displayMetrics.heightPixels

        val minWidth = fullWidth / 8f * 7
        val minHeight = fullHeight / 8f

        val currentWidth = (fullWidth - minWidth) * progress + minWidth
        val currentHeight = (fullHeight - minHeight) * progress * 1.1 + minHeight

        val marginBottomPixel = fullHeight / 4f - fullHeight / 4f * progress

        cardView.updateLayoutParams<MarginLayoutParams> {
            width = currentWidth.toInt()
            height = currentHeight.toInt()
            updateMargins(left = 0, top = 0, right = 0, bottom = marginBottomPixel.toInt())
        }

        cardView.setCardBackgroundColor(
            blendColors(
                Color.WHITE,
                Color.TRANSPARENT,
                progress
            )
        )

        cardView.requestLayout()

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