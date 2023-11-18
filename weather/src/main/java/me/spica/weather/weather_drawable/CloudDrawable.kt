package me.spica.weather.weather_drawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import me.spica.base.tools.dp
import me.spica.weather.R


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


    fun startAnim() {
        cloudAnim.start()
        cloudAnim2.start()
    }

    fun cancelAnim() {
        cloudAnim.cancel()
        cloudAnim2.cancel()
    }

    //画笔
    private val cloudPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.cloud_color)
        style = Paint.Style.FILL
    }


    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        canvas.save()
        val centerX = width / 8f * 7f
        val centerY = 0f
        canvas.translate(centerX, centerY)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
        canvas.drawCircle(
            0f, 0f,
            width / 5f + (cloudAnim2.animatedValue as Float) * 16.dp,
            cloudPaint
        )
        canvas.translate(40.dp, 0f)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
        canvas.drawCircle(
            0f, 0f,
            width / 3f + (cloudAnim.animatedValue as Float) * 8.dp,
            cloudPaint
        )
        canvas.restore()
        canvas.save()
        //=========
        canvas.translate(width / 2f, 8.dp)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
        canvas.drawCircle(
            0f, 0f,
            width / 5f + (cloudAnim.animatedValue as Float) * 5.dp,
            cloudPaint
        )
        canvas.translate(40f, -18f)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
        canvas.drawCircle(
            0f, 0f,
            width / 3f + (cloudAnim2.animatedValue as Float) * 8.dp,
            cloudPaint
        )
        canvas.restore()
        // = =====
        canvas.save()
        canvas.translate(width / 5f - 20.dp, 12.dp)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
        canvas.drawCircle(
            0f, 0f,
            width / 5f + (cloudAnim2.animatedValue as Float) * 5.dp,
            cloudPaint
        )
        canvas.translate((-40).dp, 8f)
        cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
        canvas.drawCircle(
            0f, 0f,
            width / 3f + (cloudAnim.animatedValue as Float) * 20.dp,
            cloudPaint
        )
        canvas.restore()
    }


}