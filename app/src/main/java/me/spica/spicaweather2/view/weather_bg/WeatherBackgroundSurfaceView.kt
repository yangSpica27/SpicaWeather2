package me.spica.spicaweather2.view.weather_bg

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Build.VERSION_CODES.P
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.weather_drawable.CloudDrawable
import me.spica.spicaweather2.view.weather_drawable.FoggyDrawable
import me.spica.spicaweather2.view.weather_drawable.HazeDrawable
import me.spica.spicaweather2.view.weather_drawable.RainDrawable2
import me.spica.spicaweather2.view.weather_drawable.SnowDrawable
import me.spica.spicaweather2.view.weather_drawable.SunnyDrawable
import timber.log.Timber
import java.util.UUID
import kotlin.system.measureTimeMillis

class WeatherBackgroundSurfaceView : SurfaceView, SurfaceHolder.Callback {

    @Volatile
    private var isWork = false // 是否预备绘制

    @Volatile
    private var isPause = false

    private val lock = Any()

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

    private val bgPaint = Paint(Paint.DITHER_FLAG)

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
                        hazeDrawable.startAnim()
                    }
                }
            }
        }

    private val cloudDrawable = CloudDrawable(context)

    private val foggyDrawable = FoggyDrawable(context)

    private var rainDrawable: RainDrawable2? = null

    private val snowDrawable = SnowDrawable()

    private val sunnyDrawable = SunnyDrawable(context)

    private val hazeDrawable = HazeDrawable(context)

    private var lastDrawTime = System.currentTimeMillis()

    private val drawRunnable = object : Runnable {
        override fun run() {
            synchronized(lock) {
                when (currentWeatherAnimType) {
                    NowWeatherView.WeatherAnimType.RAIN -> {
                        rainDrawable?.calculate(width, height)
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
                        this, (16 - (System.currentTimeMillis() - lastDrawTime)).coerceAtLeast(0)
                    )
                }
                lastDrawTime = System.currentTimeMillis()
            }
        }
    }

    fun getScreenCopy(foregroundBitmap: Bitmap, callbacks: (Bitmap) -> Unit) {
        val background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(
            this, background, { copyResult ->
                val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                val count = measureTimeMillis {
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
                }
                Timber.tag("图层合成耗时").e("${count}ms")
                callbacks(result)
            }, drawHandler
        )
    }

    private fun initDrawableRect(width: Int, height: Int) {
        synchronized(lock) {
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
                NowWeatherView.WeatherAnimType.SUNNY -> sunnyDrawable.doOnDraw(
                    canvas, width, height
                )

                NowWeatherView.WeatherAnimType.CLOUDY -> cloudDrawable.doOnDraw(
                    canvas, width, height
                )

                NowWeatherView.WeatherAnimType.RAIN -> rainDrawable?.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.SNOW -> snowDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.FOG -> foggyDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.UNKNOWN -> cloudDrawable.doOnDraw(
                    canvas, width, height
                )

                NowWeatherView.WeatherAnimType.HAZE -> hazeDrawable.doOnDraw(canvas, width, height)
            }
            mholder?.unlockCanvasAndPost(canvas)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        synchronized(lock) {
            rainDrawable = RainDrawable2().apply {
                ready(width, height)
                setBackgroundY(bottomY)
            }
            isWork = true
            drawThread = HandlerThread("draw-thread${UUID.randomUUID()}")
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
            rainDrawable?.release()
            rainDrawable = null
        }
    }

    // 停止所有的动画
    private fun stopAllAnim() {
        sunnyDrawable.cancelAnim()
        cloudDrawable.cancelAnim()
        foggyDrawable.cancelAnim()
        hazeDrawable.cancelAnim()
    }

    private var bottomY = 0
    fun setBackgroundY(y: Int) {
        bottomY = y
        rainDrawable?.setBackgroundY(bottomY)
        snowDrawable.setBackgroundY(bottomY)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(backgroundColorAnim.animatedValue as Int)
//        bgBitmap?.let { bgBitmap ->
//            canvas.drawBitmap(
//                bgBitmap,
//                0f, 0f, bgPaint
//            )
//        }

    }
}
