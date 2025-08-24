package me.spica.spicaweather2.view.weather_bg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.RainShaderUtils
import me.spica.spicaweather2.tools.SnowShaderUtils
import me.spica.spicaweather2.view.weather_drawable.WeatherDrawableManager
import java.util.concurrent.Executors

class WeatherBackgroundView : View, SensorEventListener {

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )


  // 天气动画管理器
  private val weatherDrawableManager = WeatherDrawableManager(context)

  private val sensorManager: SensorManager by lazy {
    context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  }


  private var rainShaderUtils: RainShaderUtils? = null

  private var snowShaderUtils: SnowShaderUtils? = null


  // 主题色
  var themeColor = ContextCompat.getColor(context, R.color.light_blue_600)


  fun setBackgroundY(y: Int) {
    // 设置背景 Y
    weatherDrawableManager.setBackgroundY(y)

  }

  // 标记颜色
  private var markerColor = Color.TRANSPARENT

  // 屏幕高度
  private val screenHeight = resources.displayMetrics.heightPixels


  var currentWeatherAnimType = NowWeatherView.WeatherAnimType.UNKNOWN
    get() = field
    set(value) {
      if (value == field) return
      field = value
      postOnAnimation {
        // 设置天气动画类型
        weatherDrawableManager.setWeatherAnimType(value)
      }
    }


  fun setMScrollY(y: Int) {
    // 设置滚动 Y
    weatherDrawableManager.setScrollY(y)
    val limit = screenHeight / 3 * 2
    markerColor =
        // 根据 Y 值计算透明度
        // 当滚动到一定值时 透明度将固定为 1
      if (y < 0) {
        ColorUtils.setAlphaComponent(themeColor, 0)
      } else if (y < limit) {
        ColorUtils.setAlphaComponent(themeColor, 255 * y / limit)
      } else {
        ColorUtils.setAlphaComponent(themeColor, 255)
      }
  }

  private lateinit var executor: java.util.concurrent.ScheduledExecutorService

  private var startTime: Long = 0

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    sensorManager.registerListener(
      this,
      sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
      SensorManager.SENSOR_DELAY_UI
    )
    executor = Executors.newSingleThreadScheduledExecutor()
    executor.scheduleWithFixedDelay({
      synchronized(this) {
        weatherDrawableManager.calculate(width, height)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (currentWeatherAnimType == NowWeatherView.WeatherAnimType.SNOW) {
            if (snowShaderUtils == null) {
              startTime = System.nanoTime()
              snowShaderUtils = SnowShaderUtils()
            }
            snowShaderUtils?.update(
              floatArrayOf(width.toFloat(), height.toFloat()),
              ((System.nanoTime() - startTime) / 1.0E10f).coerceAtMost(1f)
            )
            post {
              setRenderEffect(snowShaderUtils?.renderEffect)
            }
          } else if (snowShaderUtils != null) {
            snowShaderUtils = null
            post {
              setRenderEffect(null)
            }
          }

          if (currentWeatherAnimType == NowWeatherView.WeatherAnimType.RAIN) {
            if (rainShaderUtils == null) {
              rainShaderUtils = RainShaderUtils()
              startTime = System.nanoTime()
            }
            rainShaderUtils?.updateShader(
              (System.nanoTime() - startTime) / 1.0E9f,
            )
            post {
              (parent as View).setRenderEffect(rainShaderUtils?.renderEffect)
            }
          } else if (rainShaderUtils != null) {
            rainShaderUtils = null
            post {
              (parent as View).setRenderEffect(null)
            }
          }
        }
      }
      postInvalidateOnAnimation()
    }, 0, 8, java.util.concurrent.TimeUnit.MILLISECONDS)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    weatherDrawableManager.ready(w, h)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    sensorManager.unregisterListener(this)
    weatherDrawableManager.release()
    executor.shutdown()
    rainShaderUtils = null
    snowShaderUtils = null
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    synchronized(this) {
      if (markerColor == themeColor) {
        canvas.drawColor(themeColor)
        return
      }
//      canvas.drawColor(WeatherBackgroundSurfaceView.BACKGROUND_COLOR)
      weatherDrawableManager.doOnDraw(canvas, width, height)
      canvas.drawColor(markerColor)
    }
  }

  override fun onSensorChanged(event: SensorEvent) {
    val x: Float = event.values[SensorManager.DATA_X]
    val y: Float = event.values[SensorManager.DATA_Y]
    weatherDrawableManager.applyLinearImpulse(x, y)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      rainShaderUtils?.updateUGravity(
        x / 10f, y / 10f
      )
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit


}