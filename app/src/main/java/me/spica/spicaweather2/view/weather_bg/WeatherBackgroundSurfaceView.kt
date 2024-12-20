package me.spica.spicaweather2.view.weather_bg

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.weather_drawable.WeatherDrawableManager

class WeatherBackgroundSurfaceView :
  SurfaceView,
  SurfaceHolder.Callback {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  init {
    holder.addCallback(this)
  }

  companion object {
    // 背景色
    val BACKGROUND_COLOR = Color.parseColor("#f7f8fa")
  }

  private var backgroundColorValue = Color.parseColor("#f7f8fa")


  private val weatherDrawableManager = WeatherDrawableManager(context)

  // 通过 SimpleDrawTask 实现绘制逻辑
  private val simpleDrawTask =
    object :
      SimpleDrawTask(
        8,
        { canvas ->
          if (markerColor == backgroundColorValue) {
            canvas.drawColor(markerColor)
          } else {
            weatherDrawableManager.calculate(width, height)
            drawBackground(canvas)
            weatherDrawableManager.doOnDraw(canvas, width, height)
            canvas.drawColor(markerColor)
          }
        },
      ) {
      override fun lockCanvas(): Canvas? = this@WeatherBackgroundSurfaceView.holder.lockCanvas()

      override fun unlockCanvas(canvas: Canvas?) {
        if (canvas != null) {
          holder.unlockCanvasAndPost(canvas)
        }
      }
    }


  // 主题色
  var themeColor = ContextCompat.getColor(context, R.color.light_blue_600)


  private val backgroundColorAnim =
    ValueAnimator
      .ofArgb(
        ContextCompat.getColor(context, R.color.white),
        ContextCompat.getColor(context, R.color.white),
      ).apply {
        duration = 550
        setEvaluator(ArgbEvaluator())
        addUpdateListener {
          backgroundColorValue = it.animatedValue as Int
        }
      }

  // 开始背景色变化动画
  fun startBackgroundColorChangeAnim(
    @ColorInt fromColor: Int
  ) {
    backgroundColorAnim.setIntValues(fromColor, BACKGROUND_COLOR)
    backgroundColorAnim.start()
  }

  // 当前天气动画类型
  var currentWeatherAnimType = NowWeatherView.WeatherAnimType.UNKNOWN
    set(value) {
      if (value == field) return
      field = value
      postOnAnimation {
        weatherDrawableManager.setWeatherAnimType(value)
      }
    }

  fun getScreenCopy(
    foregroundBitmap: Bitmap,
    callbacks: (Bitmap) -> Unit,
  ) {
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    canvas.drawColor(backgroundColorValue)
    canvas.drawBitmap(foregroundBitmap, 0f, 0f, null)
    callbacks.invoke(result)
//        val background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        PixelCopy.request(
//            this, background, { copyResult ->
//                val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(result)
//                if (PixelCopy.SUCCESS == copyResult) {
//                    canvas.drawBitmap(background, 0f, 0f, null)
//                    canvas.drawBitmap(foregroundBitmap, 0f, 0f, null)
//                } else {
//                    canvas.drawColor(bgColor)
//                    canvas.drawBitmap(foregroundBitmap, 0f, 0f, null)
//                }
//                canvas.save()
//                canvas.restore()
//                callbacks(result)
//            }, Handler(Looper.getMainLooper())
//        )
  }

  override fun surfaceCreated(holder: SurfaceHolder) {
    // 渲染线程
    weatherDrawableManager.ready(width, height)
    simpleDrawTask.ready()
  }

  override fun surfaceChanged(
    holder: SurfaceHolder,
    p1: Int,
    p2: Int,
    p3: Int,
  ) = Unit

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    simpleDrawTask.destroy()
    weatherDrawableManager.release()
  }

  private var markerColor = Color.TRANSPARENT

  private val screenHeight = resources.displayMetrics.heightPixels

  fun setMScrollY(y: Int) {
    weatherDrawableManager.setScrollY(y)
    val limit = screenHeight / 3 * 2
    markerColor =
      if (y < 0) {
        ColorUtils.setAlphaComponent(backgroundColorValue, 0)
      } else if (y < limit) {
        ColorUtils.setAlphaComponent(backgroundColorValue, 255 * y / limit)
      } else {
        ColorUtils.setAlphaComponent(backgroundColorValue, 255)
      }
  }

  fun setBackgroundY(y: Int) {
    weatherDrawableManager.setBackgroundY(y)
  }

  private fun drawBackground(canvas: Canvas) {
    canvas.drawColor(backgroundColorValue)
  }
}
