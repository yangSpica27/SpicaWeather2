package me.spica.spicaweather2.view.weather_drawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.tools.getColorWithAlpha
import kotlin.math.absoluteValue

/**
 * 雾天的天气效果
 */
class HazeDrawable(
    private val context: Context,
) : WeatherDrawable() {
    private var width = 0

    private var height = 0

    private var enterProgress = 0f

    private val interpolator = OvershootInterpolator(1.2f)

    //  三组波纹的动画参数

    private val anim1 =
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateInterpolator()
        }

    private val anim2 =
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 5000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateInterpolator()
        }

    private val anim3 =
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 6700
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = OvershootInterpolator(1.2f)
        }

    override fun cancelAnim() {
        if (enterProgress == 1f) {
            enterProgress = 0f
        }
        anim1.cancel()
        anim2.cancel()
        anim3.cancel()
    }

    override fun startAnim() {
        if (!anim1.isRunning) {
            anim1.start()
        }
        if (!anim2.isRunning) {
            anim2.start()
        }
        if (!anim3.isRunning) {
            anim3.start()
        }
    }

    override fun ready(
        width: Int,
        height: Int,
    ) {
        this.width = width
        this.height = height
    }

    private var scrollY = 0

    override fun setScrollY(y: Int) {
        scrollY = y
    }

    override fun calculate(
        width: Int,
        height: Int,
    ) {
        enterProgress += .02f
        enterProgress = Math.min(1f, enterProgress)
        val animProgress = interpolator.getInterpolation(enterProgress)
        val scrollYAnimValue = scrollY.absoluteValue / height.toFloat()
        path1.reset()
        path1.moveTo(0f, 0f)
        path1.lineTo(width * 1f, 1f)
        path1.lineTo(width * 1f, height / 7f * animProgress - 30.dp * scrollYAnimValue)
        path1.cubicTo(
            width / 10f * 8f,
            height / 7f / 10f * 15f * animProgress + 24.dp * animProgress * anim1.animatedValue as Float - 28.dp * scrollYAnimValue,
            width / 10f * 3f,
            height / 7f / 10f * 5f * animProgress + 32.dp * animProgress * anim2.animatedValue as Float - 20.dp * scrollYAnimValue,
            0f,
            height / 7f / 10f * 4f * animProgress - 40.dp * scrollYAnimValue,
        )

        path2.reset()
        path2.moveTo(0f, 0f)
        path2.lineTo(width * 1f, 1f)
        path2.lineTo(width * 1f, height / 6f * animProgress - 40.dp * scrollYAnimValue)
        path2.cubicTo(
            width / 10f * 8f,
            height / 6f / 10f * 20f * animProgress + 24.dp * animProgress * anim2.animatedValue as Float - 40.dp * scrollYAnimValue,
            width / 10f * 3f,
            height / 6f / 10f * 5f * animProgress - 40.dp * animProgress * anim3.animatedValue as Float - 25.dp * scrollYAnimValue,
            0f,
            height / 7f / 10f * 4f * animProgress - 30.dp * scrollYAnimValue,
        )

        path3.reset()
        path3.moveTo(0f, 0f)
        path3.lineTo(width * 1f, 1f)
        path3.lineTo(width * 1f, height / 5f * animProgress - 24.dp * scrollYAnimValue)
        path3.cubicTo(
            width / 10f * 8f,
            height / 6f / 10f * 25f * animProgress + 24.dp * animProgress * anim3.animatedValue as Float - 40.dp * scrollYAnimValue,
            width / 10f * 3f,
            height / 5f / 10f * 5f * animProgress + 40.dp * animProgress * anim1.animatedValue as Float - 65.dp * scrollYAnimValue,
            0f,
            height / 7f / 10f * 4f * animProgress - 14.dp * scrollYAnimValue,
        )
    }

    private val path1 = Path()

    private val path2 = Path()

    private val path3 = Path()

    private val pathPaint =
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.fog_color)
            isDither = true
            isAntiAlias = true
        }

    private lateinit var bgShader: LinearGradient

    override fun doOnDraw(
        canvas: Canvas,
        width: Int,
        height: Int,
    ) {
        pathPaint.color = ContextCompat.getColor(context, R.color.fog_color)
        canvas.drawPath(path1, pathPaint)
        pathPaint.color = ContextCompat.getColor(context, R.color.fog_color)
        canvas.drawPath(path2, pathPaint)
        pathPaint.color = ContextCompat.getColor(context, R.color.fog_color)
        canvas.drawPath(path3, pathPaint)
    }
}
