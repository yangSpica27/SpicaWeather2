package me.spica.spicaweather2.view.weather_bg

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.weather_drawable.WeatherDrawableManager


class WeatherBackgroundSurfaceView : SurfaceView, SurfaceHolder.Callback, SensorEventListener {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  init {
    // 添加 SurfaceHolder 回调
    holder.addCallback(this)
  }

  companion object {
    // 背景色
    val BACKGROUND_COLOR = Color.parseColor("#f7f8fa")
  }

  // 背景色值
  private var backgroundColorValue = Color.parseColor("#f7f8fa")


  // 加速度传感器
  private val sensorManager: SensorManager by lazy {
    context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  }

  //


  // 天气动画管理器
  private val weatherDrawableManager = WeatherDrawableManager(context)

  // 通过 SimpleDrawTask 实现绘制逻辑
  private val simpleDrawTask = object : SimpleDrawTask(
    8,
    { canvas ->
      if (markerColor == backgroundColorValue) {
        // 如果标记颜色和背景颜色相同，则直接绘制标记颜色
        canvas.drawColor(markerColor)
      } else {
        // 计算天气动画
        weatherDrawableManager.calculate(width, height)
        drawBackground(canvas)
        // 执行绘制
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


  // 背景颜色动画
  private val backgroundColorAnim = ValueAnimator.ofArgb(
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
        // 设置天气动画类型
        weatherDrawableManager.setWeatherAnimType(value)
      }
    }

  fun getScreenCopy(
    foregroundBitmap: Bitmap,
    callbacks: (Bitmap) -> Unit,
  ) {
    // 创建一个位图用于存储结果
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
    // Surface 创建时调用
    // 渲染线程
    weatherDrawableManager.ready(width, height)
    simpleDrawTask.ready()
    sensorManager.registerListener(
      this,
      sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
      SensorManager.SENSOR_DELAY_UI
    )
  }

  override fun surfaceChanged(
    holder: SurfaceHolder,
    p1: Int,
    p2: Int,
    p3: Int,
  ) = Unit

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    // Surface 销毁时调用
    sensorManager.unregisterListener(this)
    simpleDrawTask.destroy()
    weatherDrawableManager.release()
  }

  // 标记颜色
  private var markerColor = Color.TRANSPARENT

  // 屏幕高度
  private val screenHeight = resources.displayMetrics.heightPixels

  fun setMScrollY(y: Int) {
    // 设置滚动 Y
    weatherDrawableManager.setScrollY(y)
    val limit = screenHeight / 3 * 2
    markerColor =
        // 根据 Y 值计算透明度
        // 当滚动到一定值时 透明度将固定为 1
      if (y < 0) {
        ColorUtils.setAlphaComponent(backgroundColorValue, 0)
      } else if (y < limit) {
        ColorUtils.setAlphaComponent(backgroundColorValue, 255 * y / limit)
      } else {
        ColorUtils.setAlphaComponent(backgroundColorValue, 255)
      }
  }

  fun setBackgroundY(y: Int) {
    // 设置背景 Y
    weatherDrawableManager.setBackgroundY(y)
  }

  private fun drawBackground(canvas: Canvas) {
    // 绘制背景
    canvas.drawColor(BACKGROUND_COLOR)
  }

  override fun onSensorChanged(event: SensorEvent) {
    val x: Float = event.values[SensorManager.DATA_X]
    val y: Float = event.values[SensorManager.DATA_Y]
    weatherDrawableManager.applyLinearImpulse(x, y)
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
