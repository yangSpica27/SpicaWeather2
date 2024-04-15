package me.spica.spicaweather2.view.weather_drawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp

/**
 * 多云动效
 */
class CloudDrawable(private val context: Context) : WeatherDrawable() {

    private val cloudAnim = ValueAnimator.ofFloat(
        0f, 1f
    ).apply {
        repeatCount = Animation.INFINITE
        repeatMode = ValueAnimator.REVERSE
        duration = 3000L
    }

    private val cloudAnim2 = ValueAnimator.ofFloat(
        0f, 1f
    ).apply {
        repeatCount = Animation.INFINITE
        repeatMode = ValueAnimator.REVERSE
        duration = 4000L
        interpolator = LinearInterpolator()
    }

    private var enterProgress = 0f

    private val overshootInterpolator = OvershootInterpolator(1.2f)
    override fun startAnim() {
        cloudAnim.start()
        cloudAnim2.start()
    }

    override fun cancelAnim() {
        cloudAnim.cancel()
        cloudAnim2.cancel()
        if (enterProgress == 1f) {
            enterProgress = 0f
        }
    }

    override fun ready(width: Int, height: Int) {

    }

    // 画笔
    private val cloudPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.cloud_color)
        style = Paint.Style.FILL
    }

    private var scrollY = 0

    override fun setScrollY(y: Int) {
        scrollY = y
    }


    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        enterProgress += .02f
        enterProgress = Math.min(1f, enterProgress)

        val animProgressValue = overshootInterpolator.getInterpolation(enterProgress)
        val scrollYAnimValue = scrollY / height.toFloat()

        canvas.translate(0f, (-40).dp + 40.dp * animProgressValue- 80.dp * scrollYAnimValue)
        canvas.save()
        val centerX = width / 8f * 7f
        val centerY = 0f
        canvas.translate(centerX, centerY)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
        canvas.drawCircle(
            0f, 0f,
            width / 5f * animProgressValue + (cloudAnim2.animatedValue as Float) * 16.dp,
            cloudPaint
        )
        canvas.translate(40.dp, 0f)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
        canvas.drawCircle(
            0f, 0f,
            width / 3f * animProgressValue + (cloudAnim.animatedValue as Float) * 8.dp,
            cloudPaint
        )
        canvas.restore()
        canvas.save()
        // =========
        canvas.translate(width / 2f, 8.dp)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
        canvas.drawCircle(
            0f, 0f,
            width / 5f * animProgressValue + (cloudAnim.animatedValue as Float) * 5.dp,
            cloudPaint
        )
        canvas.translate(40f, -18f)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
        canvas.drawCircle(
            0f, 0f,
            width / 3f * animProgressValue + (cloudAnim2.animatedValue as Float) * 8.dp * enterProgress,
            cloudPaint
        )
        canvas.restore()
        // = =====
        canvas.save()
        canvas.translate(width / 5f - 20.dp, 12.dp)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
        canvas.drawCircle(
            0f, 0f,
            width / 5f * animProgressValue + (cloudAnim2.animatedValue as Float) * 5.dp * enterProgress,
            cloudPaint
        )
        canvas.translate((-40).dp, 8f)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
        canvas.drawCircle(
            0f, 0f,
            width / 3f * animProgressValue + (cloudAnim.animatedValue as Float) * 20.dp * enterProgress,
            cloudPaint
        )
        canvas.restore()
    }
}
