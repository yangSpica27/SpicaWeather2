package me.spica.spicaweather2.view.weather_detail_card

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.tools.dp

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
        enterAnim.doOnEnd {
            enterAnim = AnimatorSet()
        }
        enterAnim.startDelay = 150
        enterAnim.start()
    }

    // 重置动画
    fun resetAnim() {
        hasInScreen = false
        animatorView.alpha = 0f

        enterAnim.playTogether(
            ObjectAnimator.ofFloat(
                animatorView,
                "alpha", 0f, 1f
            ),
            ObjectAnimator.ofFloat(
                animatorView, "translationY", 120.dp, 0f
            ).apply {
                interpolator = OvershootInterpolator(.3f * (index / 2f + 1))
            },
            ObjectAnimator.ofFloat(
                animatorView, "scaleY", 1.025f, 1f
            ).apply {
                interpolator = DecelerateInterpolator(1f)
            },
            ObjectAnimator.ofFloat(
                animatorView, "scaleX", 1.025f, 1f
            ).apply {
                interpolator = DecelerateInterpolator(1f)
            },
        )
        enterAnim.duration = 450
    }


    var hasInScreen: Boolean

    // 检查是否进入屏幕
    fun checkEnterScreen(
        isIn: Boolean
    ) {
        if (hasInScreen) {
            return
        }

        if (isIn) {
            hasInScreen = true
            doOnMainThreadIdle({
                startEnterAnim()
            },150)
        }
    }

    // 绑定数据
    fun bindData(weather: Weather)
}
