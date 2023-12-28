package me.spica.spicaweather2.view.weather_drawable

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp

/**
 * 雾天的天气效果
 */
class FoggyDrawable(private val context: Context) : WeatherDrawable() {

    // 锚点
    private val circles = arrayListOf<Circle>()

    private val anim = ObjectAnimator.ofFloat(0f, 1f)
        .apply {
            interpolator = LinearInterpolator()
            duration = 2800L
            repeatCount = Animation.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

    private val anim2 = ObjectAnimator.ofFloat(0f, 1f)
        .apply {
            interpolator = DecelerateInterpolator()
            duration = 3200L
            repeatCount = Animation.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

    fun startAnim() {
        anim.start()
        anim2.start()
    }

    fun cancelAnim() {
        anim.cancel()
        anim2.cancel()
    }

    fun ready(viewWidth: Int, viewHeight: Int) {
        synchronized(circles) {
            circles.clear()
            path.reset()
            circles.add(
                Circle(
                    PointF(
                        viewWidth / 4f,
                        viewHeight / 4f,
                    ),
                    viewHeight / 5f,
                    8.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewHeight / 6f,
                        viewHeight / 2f,
                    ),
                    viewWidth / 5.5f,
                    10.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewHeight / 4f,
                        viewHeight - viewHeight / 4f,
                    ),
                    viewHeight / 4f,
                    20.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewHeight / 3f,
                        viewHeight - viewHeight / 4f,
                    ),
                    viewHeight / 4f,
                    12.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewHeight / 2f,
                        viewHeight - viewHeight / 4f,
                    ),
                    viewHeight / 4f,
                    12.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewWidth - viewWidth / 3f,
                        viewHeight - viewHeight / 5f,
                    ),
                    viewHeight / 5f,
                    10.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewWidth - viewWidth / 6f,
                        viewHeight - viewHeight / 4f,
                    ),
                    viewHeight / 4.5f,
                    12.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewWidth - viewWidth / 5f,
                        viewHeight - viewHeight / 1.9f - 22.dp,
                    ),
                    viewHeight / 5.5f,
                    6.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewWidth - viewWidth / 3.6f + 6.dp,
                        viewHeight / 5f,
                    ),
                    viewHeight / 5f,
                    8.dp
                )
            )

            circles.add(
                Circle(
                    PointF(
                        viewWidth / 2f,
                        viewHeight / 3.5f,
                    ),
                    viewHeight / 4f,
                    10.dp
                )
            )

            path.moveTo(circles[0].centerPoint.x, circles[0].centerPoint.y)
            circles.forEach {
                path.lineTo(it.centerPoint.x, it.centerPoint.y)
            }
            path.close()
        }
    }

    private val path = Path()

    @WorkerThread
    fun calculate(viewWidth: Int, viewHeight: Int) {
        synchronized(circles) {
            circles.forEachIndexed { index, circle ->
                if (index % 3 == 0) {
                    circle.currentRadius = circle.defaultRadius - (anim.animatedValue as Float) * circle.variableRadius
                } else if (index % 3 == 1) {
                    circle.currentRadius = circle.defaultRadius - (anim2.animatedValue as Float) * circle.variableRadius
                } else {
                    circle.currentRadius = circle.defaultRadius - (anim.animatedValue as Float) * circle.variableRadius
                }
            }
        }
    }

    private val fogPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.fog_color)
        pathEffect = CornerPathEffect(12.dp)
    }

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(circles) {
            circles.forEach {
                canvas.drawCircle(
                    it.centerPoint.x,
                    it.centerPoint.y,
                    it.currentRadius,
                    fogPaint
                )
                canvas.drawPath(path, fogPaint)
            }
        }
    }

    private data class Circle(
        val centerPoint: PointF,
        val defaultRadius: Float,
        val variableRadius: Float,
        var currentRadius: Float = 0.dp
    )
}
