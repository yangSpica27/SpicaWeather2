package me.spica.spicaweather2.view.weather_bg

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build.VERSION_CODES.P
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.weather_drawable.WeatherDrawableManager
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock

class WeatherBackgroundSurfaceView : SurfaceView, SurfaceHolder.Callback {


    private val lock = ReentrantLock()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        holder.addCallback(this)
    }

    private lateinit var drawThread: HandlerThread

    private lateinit var drawHandler: Handler

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


    var bgBitmap: Bitmap? = null

    var currentWeatherAnimType = NowWeatherView.WeatherAnimType.UNKNOWN
        set(value) {
            if (value == field) return
            field = value
            post {
                weatherDrawableManager.setWeatherAnimType(value)
            }
        }


    private val weatherDrawableManager = WeatherDrawableManager(context)


    private var lastDrawTime = System.currentTimeMillis()

    private val drawRunnable = object : Runnable {
        override fun run() {
            lock.lock()
            weatherDrawableManager.calculate(width, height)
            // 执行渲染
            doOnDraw()
            if (!Thread.interrupted()) {
                drawHandler.postDelayed(
                    this, (8 - (System.currentTimeMillis() - lastDrawTime)).coerceAtLeast(0)
                )
            }
            lastDrawTime = System.currentTimeMillis()
            lock.unlock()
        }
    }

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
            }, drawHandler
        )
    }


    private var mCanvas: Canvas? = null

    private var mholder: SurfaceHolder? = null

    private fun doOnDraw() {
        mCanvas = mholder?.lockCanvas()

        // ================进行绘制==============
        mCanvas?.let { canvas ->
            drawBackground(canvas)
            weatherDrawableManager.doOnDraw(canvas, width, height)
            mholder?.unlockCanvasAndPost(canvas)
        }
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        lock.lock()
        drawThread = HandlerThread("draw-thread${UUID.randomUUID()}")
        drawThread.start()
        drawHandler = Handler(drawThread.looper)
        // 渲染线程
        weatherDrawableManager.ready(width, height)
        drawHandler.post(drawRunnable)
        this.mholder = holder
        lock.unlock()
    }

    override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        lock.lock()
        drawHandler.removeCallbacksAndMessages(null)
        drawThread.interrupt()
        drawThread.quitSafely()
        this.mholder = null
        weatherDrawableManager.release()
        lock.unlock()
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
