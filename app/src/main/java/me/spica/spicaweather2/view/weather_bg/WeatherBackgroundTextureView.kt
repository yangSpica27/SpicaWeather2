package me.spica.spicaweather2.view.weather_bg

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.SurfaceTexture
import android.view.TextureView
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.weather_drawable.WeatherDrawableManager

class WeatherBackgroundTextureView(
    context: Context,
) : HwTextureView(context),
    TextureView.SurfaceTextureListener {
    // 通过 SimpleDrawTask 实现绘制逻辑
    private val simpleDrawTask =
        object : SimpleDrawTask(16L, { canvas ->
            weatherDrawableManager.calculate(width, height)
            drawBackground(canvas)
            weatherDrawableManager.doOnDraw(canvas, width, height)
        }) {
            override fun lockCanvas(): Canvas? = this@WeatherBackgroundTextureView.lockCanvas()

            override fun unlockCanvas(canvas: Canvas?) {
                if (canvas != null) {
                    unlockCanvasAndPost(canvas)
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

    private val backgroundColorAnim =
        ValueAnimator
            .ofArgb(
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.white),
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

    fun getScreenCopy(
        foregroundBitmap: Bitmap,
        callbacks: (Bitmap) -> Unit,
    ) {
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(backgroundColorAnim.animatedValue as Int)
        canvas.drawBitmap(foregroundBitmap, 0f, 0f, null)
        callbacks.invoke(result)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(backgroundColorAnim.animatedValue as Int)
    }

    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture,
        width: Int,
        height: Int,
    ) {
        weatherDrawableManager.ready(width, height)
        simpleDrawTask.ready()
    }

    fun setMScrollY(y: Int) {
        weatherDrawableManager.setScrollY(y)
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int,
    ) = Unit

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        simpleDrawTask.destroy()
        weatherDrawableManager.release()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit

    fun setBackgroundY(y: Int) {
        weatherDrawableManager.setBackgroundY(y)
    }
}
