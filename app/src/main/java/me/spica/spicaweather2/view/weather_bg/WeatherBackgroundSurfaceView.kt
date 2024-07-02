package me.spica.spicaweather2.view.weather_bg

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.weather_drawable.WeatherDrawableManager

class WeatherBackgroundSurfaceView : SurfaceView, SurfaceHolder.Callback {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        holder.addCallback(this)
    }

    // 通过 SimpleDrawTask 实现绘制逻辑
    private val simpleDrawTask = object : SimpleDrawTask(16L, { canvas ->
        weatherDrawableManager.calculate(width, height)
        drawBackground(canvas)
        weatherDrawableManager.doOnDraw(canvas, width, height)
    }) {

        override fun lockCanvas(): Canvas? = this@WeatherBackgroundSurfaceView.holder.lockCanvas()

        override fun unlockCanvas(canvas: Canvas?) {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }


    var bgColor = ContextCompat.getColor(context, R.color.light_blue_600)
        set(value) {
            if (backgroundColorAnim.isRunning) {
                backgroundColorAnim.cancel()
            }
            backgroundColorAnim.setIntValues(backgroundColorAnim.animatedValue as Int, value)
            field = value
            backgroundColorAnim.start()
        }


    private val backgroundColorAnim = ValueAnimator.ofArgb(
        ContextCompat.getColor(context, R.color.white),
        ContextCompat.getColor(context, R.color.white)
    ).apply {
        duration = 250
        setEvaluator(ArgbEvaluator())
        start()
    }




    var currentWeatherAnimType = NowWeatherView.WeatherAnimType.UNKNOWN
        set(value) {
            if (value == field) return
            field = value
            post {
                weatherDrawableManager.setWeatherAnimType(value)
            }
        }


    private val weatherDrawableManager = WeatherDrawableManager(context)


    fun getScreenCopy(foregroundBitmap: Bitmap, callbacks: (Bitmap) -> Unit) {
        val background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(
            this, background, { copyResult ->
                val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(result)
                if (PixelCopy.SUCCESS == copyResult) {
                    canvas.drawBitmap(background, 0f, 0f, null)
                    canvas.drawBitmap(foregroundBitmap, 0f, 0f, null)
                } else {
                    canvas.drawColor(bgColor)
                    canvas.drawBitmap(foregroundBitmap, 0f, 0f, null)
                }
                canvas.save()
                canvas.restore()
                callbacks(result)
            }, Handler(Looper.getMainLooper())
        )
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        // 渲染线程
        weatherDrawableManager.ready(width, height)
        simpleDrawTask.ready()
    }

    override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) = Unit

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        simpleDrawTask.destroy()
        weatherDrawableManager.release()
    }


    fun setMScrollY(y: Int) {
        weatherDrawableManager.setScrollY(y)
    }

    fun setBackgroundY(y: Int) {
        weatherDrawableManager.setBackgroundY(y)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(backgroundColorAnim.animatedValue as Int)
    }
}
