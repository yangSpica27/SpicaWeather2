package me.spica.spicaweather2.view.weather_detail_card

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.tools.dp
import java.util.concurrent.atomic.AtomicBoolean

interface SpicaWeatherCard {
    // 卡片的View
    var animatorView: View

    // 进入动画
    var enterAnim: AnimatorSet

    // 卡片的索引
    var index: Int

    // 开始进入动画
    fun startEnterAnim() {
        enterAnim.removeAllListeners()
        enterAnim.doOnStart {
            animatorView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        enterAnim.doOnEnd {
            enterAnim = AnimatorSet()
            animatorView.setLayerType(View.LAYER_TYPE_NONE, null)
        }
        enterAnim.startDelay = 150
        enterAnim.start()
    }

    // 重置动画
    fun resetAnim() {
        hasInScreen.set(false)
        animatorView.alpha = 0f

        enterAnim.playTogether(
            ObjectAnimator.ofFloat(
                animatorView,
                "alpha",
                0f,
                1f,
            ),
            ObjectAnimator
                .ofFloat(
                    animatorView,
                    "translationY",
                    60.dp,
                    0f,
                ).apply {
                    interpolator = OvershootInterpolator(.2f * (index / 2f + 1))
                },
            ObjectAnimator
                .ofFloat(
                    animatorView,
                    "scaleY",
                    1.025f,
                    1f,
                ).apply {
                    interpolator = DecelerateInterpolator(1f)
                },
            ObjectAnimator
                .ofFloat(
                    animatorView,
                    "scaleX",
                    1.025f,
                    1f,
                ).apply {
                    interpolator = DecelerateInterpolator(1f)
                },
        )
        enterAnim.duration = 450
    }

    var hasInScreen: AtomicBoolean

    // 检查是否进入屏幕
    fun checkEnterScreen(isIn: Boolean) {
        if (hasInScreen.get()) {
            return
        }

        if (isIn) {
            hasInScreen.set(true)
            animatorView.post {
                doOnMainThreadIdle({
                    startEnterAnim()
                }, 550)
            }
        }
    }

    // 绑定数据
    fun bindData(weather: Weather)
}
