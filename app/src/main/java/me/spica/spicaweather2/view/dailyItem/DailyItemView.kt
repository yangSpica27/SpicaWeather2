package me.spica.spicaweather2.view.dailyItem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getIconRes
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.DailyWeatherBean
import me.spica.spicaweather2.tools.dp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 每日天气itemView
 */
class DailyItemView : View {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  private var dailyWeatherBean: DailyWeatherBean? = null

  private var isFirst = false

  private var maxMaxTemp = 0

  private var minMinTemp = 0

  var currentTemp: Int? = null

  private val textPaint =
    Paint().apply {
      textSize = 12.dp
      textAlign = Paint.Align.LEFT
    }

  init {
    layoutParams =
      ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, 500, false))
    } else {
      textPaint.setTypeface(Typeface.DEFAULT)
    }
    setOnClickListener {
      isExpend = !isExpend
    }
  }

  private var mHight = 50.dp

  private val itemH = 70.dp

  fun setData(
    dailyWeatherBean: DailyWeatherBean,
    isFirst: Boolean = false,
    maxTemp: Int,
    minTemp: Int,
  ) {
    this.dailyWeatherBean = dailyWeatherBean
    this.isFirst = isFirst
    this.maxMaxTemp = maxTemp
    this.minMinTemp = minTemp
    startColor =
      if (minMinTemp < 0) {
        context.getColor(R.color.light_blue_600)
      } else if (minMinTemp < 10) {
        context.getColor(R.color.light_blue_100)
      } else if (minMinTemp < 20) {
        context.getColor(R.color.light_blue_200)
      } else {
        context.getColor(R.color.light_blue_200)
      }
    endColor =
      if (maxMaxTemp < 0) {
        context.getColor(R.color.light_blue_200)
      } else if (maxMaxTemp < 10) {
        context.getColor(R.color.yellow_200)
      } else if (maxMaxTemp < 20) {
        context.getColor(R.color.yellow_500)
      } else if (maxMaxTemp < 30) {
        context.getColor(R.color.yellow_600)
      } else {
        context.getColor(R.color.yellow_600)
      }
    progressShader = null
    invalidate()
  }

  private var startColor = Color.TRANSPARENT

  private var endColor = Color.TRANSPARENT

  private var progressShader: LinearGradient? = null

  private val progressPaint =
    Paint().apply {
      strokeWidth = 8.dp
      strokeCap = Paint.Cap.ROUND
    }

  private val textBound = Rect()

  // 格式化为"周几"
  private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

  init {
    setPadding(15.dp.toInt(), 4.dp.toInt(), 15.dp.toInt(), 4.dp.toInt())
  }

  private val iconPaint = Paint()

  // 是否折叠模式
  private var isExpend = true
    set(value) {
      field = value
      if (isExpend) {
        expendAnim.setFloatValues(mHight, 50.dp)
      } else {
        expendAnim.setFloatValues(mHight, 50.dp + itemH * 3)
      }
      expendAnim.start()
    }

  private val expendAnim =
    ValueAnimator().apply {
      this.addUpdateListener {
        mHight = it.animatedValue as Float
        requestLayout()
      }
    }

  private var isFirstDraw = true

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    dailyWeatherBean?.let { dailyWeatherBean ->

      textPaint.isFakeBoldText = true
      val dateText =
        if (isFirst) {
          "今天"
        } else {
          sdfWeek.format(dailyWeatherBean.fxTime())
        }
      textPaint.color =
        if (isFirst) {
          WeatherCodeUtils.getWeatherCode(dailyWeatherBean.iconId).getThemeColor()
        } else {
          ContextCompat.getColor(context, R.color.textColorPrimary)
        }

      textPaint.textSize = 16.dp
      textPaint.getTextBounds(dateText, 0, dateText.length, textBound)
      canvas.drawText(
        dateText,
        paddingStart * 1f,
        45.dp / 2f + textBound.height() / 2f,
        textPaint,
      )

      canvas.save()

      val drawProgressIconBitmap =
        getOrCreateBitmap(R.drawable.ic_down, width = 20.dp.toInt(), height = 20.dp.toInt())
      canvas.translate(
        width - paddingRight - drawProgressIconBitmap.width * 1f,
        45.dp / 2f - drawProgressIconBitmap.height / 2,
      )
      if (isFirstDraw) {
        canvas.rotate(
          0f,
          drawProgressIconBitmap.width / 2f,
          drawProgressIconBitmap.height / 2f,
        )
        isFirstDraw = false
      } else {
        if (!isExpend) {
          canvas.rotate(
            90f * expendAnim.animatedFraction,
            drawProgressIconBitmap.width / 2f,
            drawProgressIconBitmap.height / 2f,
          )
        } else {
          canvas.rotate(
            90 - 90f * expendAnim.animatedFraction,
            drawProgressIconBitmap.width / 2f,
            drawProgressIconBitmap.height / 2f,
          )
        }
      }
      canvas.drawBitmap(
        drawProgressIconBitmap,
        0f,
        0f,
        iconPaint,
      )

      canvas.restore()

      textPaint.color = ContextCompat.getColor(context, R.color.textColorPrimary)
      textPaint.textSize = 16.dp
      val maxTempText = "${dailyWeatherBean.maxTemp}℃"
      textPaint.getTextBounds(maxTempText, 0, maxTempText.length, textBound)
      canvas.drawText(
        maxTempText,
        width - paddingRight - drawProgressIconBitmap.width * 1f - 30.dp - 8.dp,
        45.dp / 2f + textBound.height() / 2f,
        textPaint,
      )

      textPaint.color = ContextCompat.getColor(context, R.color.textColorPrimary)
      textPaint.textSize = 16.dp
      val minTempText = "${dailyWeatherBean.minTemp}℃"
      textPaint.getTextBounds(minTempText, 0, minTempText.length, textBound)
      canvas.drawText(
        minTempText,
        width - paddingRight - drawProgressIconBitmap.width * 1f - 8.dp - 100.dp - 30.dp * 2,
        45.dp / 2f + textBound.height() / 2f,
        textPaint,
      )

      // 绘制天气图标
      val bitmap =
        getOrCreateBitmap(
          WeatherCodeUtils.getWeatherCode(dailyWeatherBean.iconId).getIconRes(),
          width = 35.dp.toInt(),
          height = 35.dp.toInt(),
        )

      canvas.drawBitmap(
        bitmap,
        (width - paddingRight - drawProgressIconBitmap.width * 1f - 8.dp - 100.dp - 30.dp * 2) / 2f -
            bitmap.width / 2f +
            paddingLeft / 2f + bitmap.width / 2f,
        45.dp / 2f - bitmap.height / 2,
        iconPaint,
      )

      val left =
        width - paddingRight - drawProgressIconBitmap.width * 1f - 8.dp - 100.dp - 30.dp + 8.dp + 12.dp
      val right =
        width - paddingRight - drawProgressIconBitmap.width * 1f - 30.dp - 8.dp - 12.dp

      progressPaint.shader = null
      progressPaint.strokeWidth = 8.dp
      progressPaint.color = ContextCompat.getColor(context, R.color.rainRectColor)

      canvas.drawLine(
        left,
        45.dp / 2f,
        right,
        45.dp / 2f,
        progressPaint,
      )

      if (progressShader == null) {
        progressShader =
          LinearGradient(
            left,
            0F,
            right,
            0f,
            startColor,
            endColor,
            Shader.TileMode.CLAMP,
          )
      }

      progressPaint.color = ContextCompat.getColor(context, R.color.black)
      progressPaint.shader = progressShader

      val left2 =
        (dailyWeatherBean.minTemp - minMinTemp) * 1f / (maxMaxTemp - minMinTemp) * (right - left) + left
      val right2 =
        (dailyWeatherBean.maxTemp - minMinTemp) * 1f / (maxMaxTemp - minMinTemp) * (right - left) + left

      canvas.drawLine(
        left2,
        45.dp / 2f,
        right2,
        45.dp / 2f,
        progressPaint,
      )

      if (currentTemp != null) {
        progressPaint.shader = null
        val currentX =
          (currentTemp!! - minMinTemp) * 1f / (maxMaxTemp - minMinTemp) * (right - left) + left
        progressPaint.color = Color.WHITE
        canvas.drawCircle(currentX, 45.dp / 2f, 8.dp, progressPaint)
        progressPaint.shader = progressShader
        canvas.drawCircle(currentX, 45.dp / 2f, 6.dp, progressPaint)
      }
    }
    if (!isExpend || (expendAnim.isRunning)) {
      // 绘制下半部分

      drawItem(
        canvas,
        "湿度",
        "${dailyWeatherBean?.water ?: "--"}%",
        R.drawable.ic_water,
        paddingLeft * 1f,
        width / 2f - 2.dp,
        45.dp + 5.dp,
      )

      drawItem(
        canvas,
        "降水量",
        "${dailyWeatherBean?.precip ?: "--"}mm",
        R.drawable.ic_heavy_rain_outline,
        width / 2f + 2.dp,
        width - paddingRight * 1f,
        45.dp + 5.dp,
      )

      drawItem(
        canvas,
        "日间风速",
        "${dailyWeatherBean?.windSpeed ?: "--"}km/h",
        R.drawable.ic_wind2,
        paddingLeft * 1f,
        width / 2f - 2.dp,
        45.dp + 5.dp + itemH,
      )

      drawItem(
        canvas,
        "紫外线强度",
        dailyWeatherBean?.uv ?: "--",
        R.drawable.ic_plastic_surgery,
        width / 2f + 2.dp,
        width - paddingRight * 1f,
        45.dp + 5.dp + itemH,
      )

      drawItem(
        canvas,
        "能见度",
        "${dailyWeatherBean?.vis ?: "--"}km",
        R.drawable.ic_texture,
        paddingLeft * 1f,
        width / 2f - 2.dp,
        45.dp + 5.dp + itemH * 2,
      )

      drawItem(
        canvas,
        "云层覆盖率",
        "${dailyWeatherBean?.cloud ?: "--"}%",
        R.drawable.ic_cloud_outline,
        width / 2f + 2.dp,
        width - paddingRight * 1f,
        45.dp + 5.dp + itemH * 2,
      )
    }
  }

  private val textRect = Rect()

  private fun drawItem(
    canvas: Canvas,
    title: String,
    text: String,
    iconRes: Int,
    left: Float,
    right: Float,
    top: Float,
  ) {
    val bitmap = getOrCreateBitmap(iconRes, width = 24.dp.toInt(), height = 24.dp.toInt())
    canvas.drawBitmap(
      bitmap,
      left + 8.dp,
      top + 65.dp / 2f + -bitmap.height / 2,
      iconPaint,
    )
    canvas.drawRoundRect(
      left,
      top,
      right,
      top + 65.dp,
      8.dp,
      8.dp,
      rectPaint,
    )

    itemTextPaint.textSize = 17.dp
    itemTextPaint.typeface = Typeface.DEFAULT_BOLD
    itemTextPaint.color = ContextCompat.getColor(context, R.color.textColorPrimary)
    itemTextPaint.getTextBounds(title, 0, title.length, textRect)
    canvas.drawText(
      title,
      left + 8.dp + bitmap.width + 10.dp,
      top + 12.dp + textRect.height(),
      itemTextPaint,
    )
    itemTextPaint.color = ContextCompat.getColor(context, R.color.textColorPrimaryHint)
    itemTextPaint.typeface = Typeface.DEFAULT
    itemTextPaint.textSize = 16.dp
    itemTextPaint.getTextBounds(text, 0, text.length, textRect)
    canvas.drawText(
      text,
      left + 8.dp + bitmap.width + 10.dp,
      top + 65.dp - 12.dp,
      itemTextPaint,
    )
  }

  private val itemTextPaint =
    Paint().apply {
      textSize = 16.dp
      textAlign = Paint.Align.LEFT
    }

  private val rectPaint =
    Paint().apply {
      color = Color.parseColor("#1a4a4a4a")
    }

  companion object {
    private val bitmapPool = LruCache<Int, Bitmap>(4 * 1024 * 1024)
  }

  private fun getOrCreateBitmap(
    iconRes: Int,
    width: Int = 25.dp.toInt(),
    height: Int = 25.dp.toInt(),
  ): Bitmap {
    if (bitmapPool[iconRes] != null) {
      return bitmapPool[iconRes]
    }
    val bitmap =
      ContextCompat
        .getDrawable(
          context,
          iconRes,
        )!!
        .toBitmap(
          width = width,
          height = height,
          config = Bitmap.Config.ARGB_8888,
        )
    bitmapPool.put(iconRes, bitmap)
    return bitmap
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    invalidate()
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    setMeasuredDimension(
      MeasureSpec.getSize(widthMeasureSpec),
      mHight.toInt(),
    )
  }
}
