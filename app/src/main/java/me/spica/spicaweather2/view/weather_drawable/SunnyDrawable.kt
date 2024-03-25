package me.spica.spicaweather2.view.weather_drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp
import timber.log.Timber

/**
 * 晴天的天气效果
 */
class SunnyDrawable(private val context: Context) : WeatherDrawable() {

    // 绘制太阳的paint
    private val sunnyPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.l8)
    }

    private val overshootInterpolator = OvershootInterpolator(1.2f)
    

    private var rotation = 0f

    private var enterProgress = 0f

    fun startAnim() {
    }

    fun cancelAnim() {
        if (enterProgress == 1f) {
            enterProgress = 0f
        }
    }

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        rotation += .004f
        rotation /= 1

        enterProgress += .02f
        enterProgress = Math.min(1f, enterProgress)

        val enterAnimaValue  = overshootInterpolator.getInterpolation(enterProgress)
        

        sunnyPaint.color = ContextCompat.getColor(context, R.color.sun_light_color)
        val centerX = width - 50.dp + 150.dp * (1f - enterAnimaValue)
        val centerY = 0 + 50.dp - 150.dp * (1f -  enterAnimaValue)
        // 保存画布的位置
        canvas.save()

        // 将画布位移到右上角方便测量
        for (index in 1..4) {
            canvas.translate(centerX, centerY)
            // 将画布旋转三次 每次30度（正方形旋转90度看上去一致）+动画进度x90度（用于旋转）
            canvas.rotate(index * (30) + 90 * (rotation))
            // 绘制内外三层矩形
            val smallSize = 100.dp  * enterAnimaValue
            val midSize = 180.dp  * enterAnimaValue
            val largeSize = 300.dp  * enterAnimaValue
            canvas.drawRect(-smallSize, -smallSize, smallSize, smallSize, sunnyPaint)
            canvas.drawRect(-midSize, -midSize, midSize, midSize, sunnyPaint)
            canvas.drawRect(-largeSize, -largeSize, largeSize, largeSize, sunnyPaint)
            canvas.restore()
            canvas.save()
        }
        // 画布复位用于后面的绘制
        canvas.restore()
    }
}
