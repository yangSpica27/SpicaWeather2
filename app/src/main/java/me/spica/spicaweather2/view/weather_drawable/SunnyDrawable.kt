package me.spica.spicaweather2.view.weather_drawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp

/**
 * 晴天的天气效果
 */
class SunnyDrawable(private val context: Context) : WeatherDrawable() {

    // 绘制太阳的paint
    private val sunnyPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.sun_light_color)
        style = Paint.Style.FILL
    }

    private val enterInterpolator = OvershootInterpolator(1.2f)


    private var rotation = 0f

    private var enterProgress = 0f


    private val rippleAnim = ValueAnimator.ofFloat(
        0f, 1f
    ).apply {
        repeatCount = Animation.INFINITE
        repeatMode = ValueAnimator.REVERSE
        duration = 1400L
    }

    private val rippleAnim2 = ValueAnimator.ofFloat(
        0f, 1f
    ).apply {
        repeatCount = Animation.INFINITE
        repeatMode = ValueAnimator.REVERSE
        duration = 2000L
        interpolator = LinearInterpolator()
    }


    fun startAnim() {
        rippleAnim.start()
        rippleAnim2.start()
    }

    fun cancelAnim() {
        if (enterProgress == 1f) {
            enterProgress = 0f
        }
        rippleAnim.start()
        rippleAnim2.start()
    }

    // 从里到外四层
    private val path1 = Path()
    private val path2 = Path()
    private val path3 = Path()
    private val path4 = Path()


    private fun initPath(width: Int, height: Int) {
        path1.reset()
        path2.reset()
        path3.reset()
        path4.reset()

        path1.moveTo(width * 1f, 0f)
        path1.lineTo(width * 1f, width * 1f / 10f * 6f + 20.dp * rippleAnim2.animatedValue as Float)
        path1.cubicTo(
            width * 1f / 10f * 9f,
            width * 1f / 10f * 6f + 12.dp * rippleAnim2.animatedValue as Float,
            width * 1f / 10f * 4f,
            width * 1f / 10f * 5f + 10.dp * rippleAnim2.animatedValue as Float,
            width / 10f * 4f - 30.dp * rippleAnim2.animatedValue as Float,
            0f
        )

        path2.moveTo(width * 1f, 0f)
        path2.lineTo(width * 1f, width * 1f / 10f * 7f + 25.dp * rippleAnim2.animatedValue as Float)
        path2.cubicTo(
            width * 1f / 10f * 8f,
            width * 1f / 10f * 7f + 20.dp * rippleAnim2.animatedValue as Float,
            width * 1f / 10f * 4f,
            width * 1f / 10f * 6f + 15.dp * rippleAnim2.animatedValue as Float,
            width / 10f * 4f - 20.dp * rippleAnim.animatedValue as Float,
            0f
        )

        path3.moveTo(width * 1f, 0f)
        path3.lineTo(width * 1f, width * 1f / 10f * 6f + 20.dp * rippleAnim.animatedValue as Float)
        path3.cubicTo(
            width * 1f / 10f * 8f,
            width * 1f / 10f * 6f + 15.dp * rippleAnim2.animatedValue as Float,
            width * 1f / 10f * 4f,
            width * 1f / 10f * 5f + 20.dp * rippleAnim2.animatedValue as Float,
            width / 10f * 3f - 24.dp * rippleAnim2.animatedValue as Float,
            0f
        )

        path4.moveTo(width * 1f, 0f)
        path4.lineTo(width * 1f, width * 1f / 10f * 8f + 40.dp * rippleAnim.animatedValue as Float)
        path4.cubicTo(
            width * 1f / 10f * 8f,
            width * 1f / 10f * 8f + 40.dp * rippleAnim.animatedValue as Float,
            width * 1f / 10f * 2f,
            width * 1f / 10f * 6f + 40.dp * rippleAnim.animatedValue as Float,
            width / 10f * 2f - 40.dp * rippleAnim.animatedValue as Float,
            0f
        )
    }


    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        rotation += .004f
        rotation /= 1

        enterProgress += .02f
        enterProgress = Math.min(1f, enterProgress)
        val enterAnimaValue = enterInterpolator.getInterpolation(enterProgress)
        canvas.save()
        canvas.translate(width * 1f, 0f)
        canvas.scale(enterAnimaValue, enterAnimaValue)
        canvas.translate(-width * 1f, 0f)
        initPath(width, height)
//        sunnyPaint.alpha = (100 * enterAnimaValue).toInt()
        canvas.drawPath(path1, sunnyPaint)
//        sunnyPaint.alpha = (80 * enterAnimaValue).toInt()
        canvas.drawPath(path2, sunnyPaint)
//        sunnyPaint.alpha = (60 * enterAnimaValue).toInt()
        canvas.drawPath(path3, sunnyPaint)
//        sunnyPaint.alpha = (40 * enterAnimaValue).toInt()
        canvas.drawPath(path4, sunnyPaint)
        canvas.restore()
    }
}
