package me.spica.spicaweather2.view.weather_bg

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.view.weather_drawable.CloudDrawable
import me.spica.spicaweather2.view.weather_drawable.FoggyDrawable
import me.spica.spicaweather2.view.weather_drawable.HazeDrawable
import me.spica.spicaweather2.view.weather_drawable.RainDrawable
import me.spica.spicaweather2.view.weather_drawable.SnowDrawable
import me.spica.spicaweather2.view.weather_drawable.SunnyDrawable
import java.util.UUID

class WeatherBackgroundSurfaceView : SurfaceView, SurfaceHolder.Callback {

    @Volatile
    private var isWork = false // 是否预备绘制

    @Volatile
    private var isPause = false

    private val lock = Any()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        holder.addCallback(this)
    }

    private lateinit var drawThread: HandlerThread

    private lateinit var drawHandler: Handler

    var bgColor = ContextCompat.getColor(context, android.R.color.transparent)

    private val bgPaint = Paint(Paint.DITHER_FLAG)

    var bgShader: LinearGradient? = null
        set(value) {
            field = value
            bgPaint.shader = value
        }

    var bgBitmap: Bitmap? = null

    var currentWeatherAnimType = NowWeatherView.WeatherAnimType.UNKNOWN
        set(value) {
            if (value == field) return
            field = value
            post {
                when (value) {
                    NowWeatherView.WeatherAnimType.SUNNY -> {
                        stopAllAnim()
                        sunnyDrawable.startAnim()
                    }

                    NowWeatherView.WeatherAnimType.CLOUDY -> {
                        stopAllAnim()
                        cloudDrawable.startAnim()
                    }

                    NowWeatherView.WeatherAnimType.RAIN -> {
                        stopAllAnim()
                    }

                    NowWeatherView.WeatherAnimType.SNOW -> {
                        stopAllAnim()
                    }

                    NowWeatherView.WeatherAnimType.UNKNOWN -> {
                        stopAllAnim()
                        cloudDrawable.startAnim()
                    }

                    NowWeatherView.WeatherAnimType.FOG -> {
                        stopAllAnim()
                        foggyDrawable.startAnim()
                    }

                    NowWeatherView.WeatherAnimType.HAZE -> {
                        stopAllAnim()
                    }
                }
            }
        }

    private val cloudDrawable = CloudDrawable(context)

    private val foggyDrawable = FoggyDrawable(context)

    private val rainDrawable = RainDrawable()

    private val snowDrawable = SnowDrawable()

    private val sunnyDrawable = SunnyDrawable(context)

    private val hazeDrawable = HazeDrawable(context)

    private var lastDrawTime = System.currentTimeMillis()

    private val drawRunnable = object : Runnable {
        override fun run() {
            synchronized(lock) {
                when (currentWeatherAnimType) {
                    NowWeatherView.WeatherAnimType.RAIN -> {
                        rainDrawable.calculate(width, height)
                    }

                    NowWeatherView.WeatherAnimType.SNOW -> {
                        snowDrawable.calculate(width, height)
                    }

                    NowWeatherView.WeatherAnimType.FOG -> {
                        foggyDrawable.calculate(width, height)
                    }

                    NowWeatherView.WeatherAnimType.HAZE -> {
                        hazeDrawable.calculate()
                    }

                    else -> {}
                }
                // 执行渲染
                doOnDraw()
                if (drawThread.isAlive) {
                    drawHandler.postDelayed(
                        this, (16 - (System.currentTimeMillis() - lastDrawTime))
                            .coerceAtLeast(0)
                    )
                }
                lastDrawTime = System.currentTimeMillis()
            }
        }
    }

    fun getScreenCopy(foregroundBitmap: Bitmap, callbacks: (Bitmap) -> Unit) {
        val background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(
            this, background,
            { copyResult ->
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
            },
            handler
        )
    }

    private fun initDrawableRect(width: Int, height: Int) {
        synchronized(lock) {
            rainDrawable.ready(width, height)
            snowDrawable.ready(width, height)
            foggyDrawable.ready(width, height)
            hazeDrawable.ready(width, height)
        }
    }

    private var mCanvas: Canvas? = null

    private var mholder: SurfaceHolder? = null

    private fun doOnDraw() {
        mCanvas = mholder?.lockCanvas()

        // ================进行绘制==============
        mCanvas?.let { canvas ->
            drawBackground(canvas)

            if (isPause) {
                mholder?.unlockCanvasAndPost(canvas)
                return
            }
            when (currentWeatherAnimType) {
                NowWeatherView.WeatherAnimType.SUNNY -> sunnyDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.CLOUDY -> cloudDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.RAIN -> rainDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.SNOW -> snowDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.FOG -> foggyDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.UNKNOWN -> cloudDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.HAZE -> hazeDrawable.doOnDraw(canvas, width, height)
            }
            mholder?.unlockCanvasAndPost(canvas)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        synchronized(lock) {
            isWork = true
            drawThread = HandlerThread("draw-thread${UUID.randomUUID()}", Thread.NORM_PRIORITY)
            drawThread.start()
            drawHandler = Handler(drawThread.looper)
            // 渲染线程
            drawHandler.post(drawRunnable)
            this.mholder = holder
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        initDrawableRect(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        synchronized(lock) {
            isWork = false
            drawHandler.removeCallbacksAndMessages(null)
            drawThread.quitSafely()
            this.mholder = null
        }
    }

    // 停止所有的动画
    private fun stopAllAnim() {
        sunnyDrawable.cancelAnim()
        cloudDrawable.cancelAnim()
        foggyDrawable.cancelAnim()
    }

    fun setBackgroundY(y: Int) {
        rainDrawable.setBackgroundY(y)
        snowDrawable.setBackgroundY(y)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(bgColor)
        bgBitmap?.let { bgBitmap ->
            canvas.drawBitmap(
                bgBitmap,
                0f, 0f, bgPaint
            )
        }

    }
}
