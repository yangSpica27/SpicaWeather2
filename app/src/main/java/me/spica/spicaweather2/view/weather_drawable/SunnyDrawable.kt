package me.spica.spicaweather2.view.weather_drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
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



    private var rotation = 0f

    fun startAnim() {
        // NOTHING
    }

    fun cancelAnim() {
        // NOTHING
    }

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        rotation +=.004f
        rotation /= 1
        Timber.tag("角度").e("$rotation°")
        sunnyPaint.alpha = 20
        sunnyPaint.color = ContextCompat.getColor(context, R.color.sun_light_color)
        val centerX = width - 50.dp
        val centerY = 0 + 50.dp
        // 保存画布的位置
        canvas.save()

        // 将画布位移到右上角方便测量
        for (index in 1..4) {
            canvas.translate(centerX, centerY)
            // 将画布旋转三次 每次30度（正方形旋转90度看上去一致）+动画进度x90度（用于旋转）
            canvas.rotate(index * (30) + 90 * (rotation))
            // 绘制内外三层矩形
            val smallSize = 100.dp
            val midSize = 180.dp
            val largeSize = 300.dp
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
