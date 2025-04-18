package me.spica.spicaweather2.view.line

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader.TileMode
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewParent
import android.widget.OverScroller
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager2.widget.ViewPager2
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getIconRes
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.HourlyWeatherBean
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.tools.getColorWithAlpha
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * 逐小时天气折线图
 */
class HourlyLineView : View {
  private val mScroller: OverScroller = OverScroller(context)
  private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()

  private val maximumFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
  private val minimumFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity

  // 手的移动要大于这个距离才开始移动控件
  private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  private val gapWidth = 50f
  private var offset = 0f
  private var lastX = 0f
  private var lastY = 0f

  private val data: ArrayList<HourlyWeatherBean> = arrayListOf() // 数据源

  private val topIconPaint = Paint()

  private val tempPaint =
    TextPaint().apply {
      typeface = Typeface.DEFAULT_BOLD
      textSize = 16.dp
      color = ContextCompat.getColor(context, R.color.text_color_white)
      textAlign = Paint.Align.CENTER
    }

  private val lineBgPaint =
    Paint().apply {
      color = ContextCompat.getColor(context, R.color.text_color_white)
      style = Paint.Style.FILL
    }

  private val linePaint =
    Paint().apply {
      strokeWidth = 3.dp
      style = Paint.Style.STROKE
      strokeCap = Paint.Cap.ROUND
    }

  private val itemWidth = 75.dp // 每个单元的宽度

  // 预留给温度数字的绘制高度
  private val tempTextHeight = 55.dp

  // 预留给线条绘制的限制
  private val lineHeight = 70.dp

  private val topIconHeight = 40.dp

  private val popHeight = 35.dp

  private val weatherNameHeight = 0.dp

  private val timeHeight = 25.dp

  private var maxTemp = 0

  private var minTemp = 0

  init {
    setPadding(14.dp.toInt(), 12.dp.toInt(), 14.dp.toInt(), 12.dp.toInt())
  }

  fun setData(data: List<HourlyWeatherBean>) {
    this.data.clear()
    this.data.addAll(data)
    initParam()
    if (cacheBitmap?.isRecycled == false) {
      cacheBitmap?.recycle()
    }
    cacheBitmap = null
    invalidate()
  }

  private val mPointList: MutableList<Point> = arrayListOf()

  private val rainPointList: MutableList<Point> = arrayListOf()

  private val colors = arrayListOf<Int>()

  // 绘制温度线
  private val tempLinePath = Path()

  // 用于绘制背景shader
  private val tempLineBgPath = Path()

  private val rainLinePath = Path()

  private val bitmaps = arrayListOf<Bitmap>()

  private val shaders = arrayListOf<LinearGradient>()

  private fun initParam() {
    if (data.isEmpty()) return
    maxTemp = data[0].temp
    minTemp = data[0].temp
    mPointList.clear()
    colors.clear()
    tempLinePath.reset()
    tempLineBgPath.reset()
    rainLinePath.reset()
    bitmaps.clear()
    shaders.clear()
    // 获取极值用于计算锚点
    this.data.forEach { item ->
      maxTemp = Math.max(maxTemp, item.temp)
      minTemp = Math.min(minTemp, item.temp)
      bitmaps.add(
        ContextCompat
          .getDrawable(
            context,
            WeatherCodeUtils.getWeatherCode(item.iconId).getIconRes(),
          )!!
          .toBitmap(40.dp.toInt(), 40.dp.toInt(), Bitmap.Config.ARGB_8888),
      )
    }

    rainPointList.clear()
    // 计算锚点
    data.forEachIndexed { index, item ->
      val x = paddingLeft + index * itemWidth + itemWidth / 2f
      val tempFraction = (item.temp - minTemp) * 1f / (maxTemp - minTemp)
      val y =
        paddingTop + 12.dp + 24.dp + tempTextHeight + 12.dp + lineHeight - lineHeight * tempFraction - 12.dp
      mPointList.add(Point(x.toInt(), y.toInt()))

      val rainX = paddingLeft + index * itemWidth + itemWidth / 2f
      val rainFraction = (item.pop) / 100f
      val rainY =
        paddingTop + 12.dp + 24.dp + tempTextHeight + 12.dp + lineHeight - lineHeight * rainFraction - 12.dp
      rainPointList.add(Point(rainX.toInt(), rainY.toInt()))
      val themeColor = WeatherCodeUtils.getWeatherCode(iconId = item.iconId).getThemeColor()
      colors.add(themeColor)
      // 生成游标到对应位置渐变色
      shaders.add(
        LinearGradient(
          0f,
          0f,
          0f,
          paddingTop + paddingBottom + topIconHeight + tempTextHeight + lineHeight + popHeight + weatherNameHeight + timeHeight,
          intArrayOf(
            Color.TRANSPARENT,
            ColorUtils.setAlphaComponent(Color.WHITE, 55),
            Color.TRANSPARENT,
          ),
          floatArrayOf(
            0f,
            .5f,
            1f,
          ),
          TileMode.CLAMP,
        ),
      )
    }
    val themeColor =
      WeatherCodeUtils
        .getWeatherCode(iconId = data.firstOrNull()?.iconId ?: 101)
        .getThemeColor()
    linePaint.color = ContextCompat.getColor(context, R.color.text_color_white)

    lineBgPaint.shader =
      LinearGradient(
        0f,
        paddingTop + 12.dp + 24.dp +
            tempTextHeight + 12.dp +
            lineHeight - 12.dp - lineHeight,
        0f,
        paddingTop + 12.dp + 24.dp +
            tempTextHeight + 12.dp +
            lineHeight - 12.dp,
        intArrayOf(
          ColorUtils.setAlphaComponent(
            ContextCompat.getColor(context, R.color.text_color_white) ?: Color.TRANSPARENT, 120
          ),
          ColorUtils.setAlphaComponent(
            ContextCompat.getColor(context, R.color.text_color_white) ?: Color.TRANSPARENT, 0
          ),
        ),
        floatArrayOf(0f, 1f),
        TileMode.CLAMP,
      )

    mPointList.forEachIndexed { index, point ->
      // 上个点
      val lastPoint: Point
      // 上上个点
      val lastLastPoint: Point
      // 下一个点
      val nextPoint: Point
      // 前2点 最后面的点 缺少点用当前点补缺
      when (index) {
        0 -> {
          // 起始点
          lastPoint = Point(point.x, point.y)
          lastLastPoint = Point(point.x, point.y)
          nextPoint = Point(mPointList[index + 1].x, mPointList[index + 1].y)
          tempLinePath.moveTo(point.x * 1f, point.y * 1f)
        }

        1 -> {
          lastPoint = Point(point.x, point.y)
          lastLastPoint = Point(point.x, point.y)
          nextPoint = Point(mPointList[index + 1].x, mPointList[index + 1].y)
        }

        mPointList.size - 1 -> {
          // 结束点
          lastLastPoint = Point(mPointList[index - 2].x, mPointList[index - 2].y)
          lastPoint = Point(mPointList[index - 1].x, mPointList[index - 1].y)
          nextPoint = Point(point.x, point.y)
        }

        else -> {
          // 中间点
          lastLastPoint = Point(mPointList[index - 2].x, mPointList[index - 2].y)
          lastPoint = Point(mPointList[index - 1].x, mPointList[index - 1].y)
          nextPoint = Point(mPointList[index + 1].x, mPointList[index + 1].y)
        }
      }

      if (index != 0) {
        // 第一个点是起始点 不用计算曲线 从第二点开始
        // 求出两个锚点的坐标差
        val firstDiffX: Float = point.x * 1f - lastLastPoint.x
        val firstDiffY: Float = point.y * 1f - lastLastPoint.y

        val secondDiffX: Float = nextPoint.x * 1f - lastPoint.x
        val secondDiffY: Float = nextPoint.y * 1f - lastPoint.y

        // 根据锚点间坐标差 求出 两个控制点 参数给的越大 斜率越小
        val controlPointLeft =
          PointF(
            lastPoint.x + 0.1f * firstDiffX,
            lastPoint.y + 0.1f * firstDiffY,
          )
        val controlPointRight =
          PointF(
            point.x - 0.1f * secondDiffX,
            point.y - 0.1f * secondDiffY,
          )
        // 做二阶贝塞尔
        tempLinePath.cubicTo(
          controlPointLeft.x,
          controlPointLeft.y,
          controlPointRight.x,
          controlPointRight.y,
          point.x * 1f,
          point.y * 1f,
        )
      }
    }
    tempLineBgPath.addPath(tempLinePath)
    tempLineBgPath.lineTo(
      mPointList.last().x.toFloat(),
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp,
    )
    tempLineBgPath.lineTo(
      mPointList.first().x.toFloat(),
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp,
    )
    tempLineBgPath.close()
    invalidate()
  }

  private var cacheBitmap: Bitmap? = null

  private var cursorPaint =
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = ContextCompat.getColor(context, R.color.white)
      strokeWidth = itemWidth
    }

  private val baseLinePaint =
    Paint().apply {
      color = ContextCompat.getColor(context, R.color.text_color_white)
      strokeWidth = 2f
      pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (data.isEmpty() || width == 0) return
    // 读取缓存图片
    if (cacheBitmap == null) {
      // 没有缓存就画一遍 然后画完生成位图保存 后面直接加载图片就可以了 避免重复绘制工作
      cacheBitmap =
        createBitmap((paddingLeft + paddingRight + mPointList.size * itemWidth).toInt(), height)
      val cacheCanvas = Canvas(cacheBitmap!!)
      drawBaseLine(cacheCanvas)
      drawTempLine(cacheCanvas)
      drawRainLine(cacheCanvas)
      mPointList.forEachIndexed { index, point ->
        // 提示文字
        drawTempText(cacheCanvas, index, point)
        // 图标
        drawIcon(cacheCanvas, index, point)
        // 概率
        drawPop(cacheCanvas, index, point)
        // 天气数据
//                drawWeatherName(cacheCanvas, index, point)
        // 时间
        drawTime(cacheCanvas, point, index)
      }
      // 保存绘制内容到位图
      cacheCanvas.save()
      cacheCanvas.restore()
    }
    canvas.save()
    // 根据用户手指滑动距离，移动画布，看上去就是跟随移动了
    canvas.translate(-offset, 0f)
    // 绘制图片
    canvas.drawBitmap(cacheBitmap!!, 0f, 0f, topIconPaint)
    canvas.restore()
    // 获取当前应绘制X
    val scrollMaxWidth = paddingLeft + paddingRight + mPointList.size * itemWidth - width
    val currentX =
      (offset / scrollMaxWidth) * (width - paddingLeft - paddingRight - itemWidth) + itemWidth / 2f + paddingLeft

    val currentIndex =
      Math.min(
        Math.abs((currentX + offset - itemWidth / 2f - paddingLeft) / itemWidth),
        data.size - 1f,
      )

    cursorPaint.shader = shaders[currentIndex.toInt()]

    canvas.drawRoundRect(
      currentX - itemWidth / 2f,
      height - timeHeight,
      currentX + itemWidth / 2f,
      paddingTop * 1f,
      12.dp,
      12.dp,
      cursorPaint,
    )
  }

  private fun getItemCenterX(index: Int): Float =
    paddingLeft + index * itemWidth + itemWidth / 2f - offset

  private val popTextPaint =
    TextPaint().apply {
      textSize = 15.dp
      color = ContextCompat.getColor(context, R.color.text_color_white)
      typeface = Typeface.DEFAULT_BOLD
      textAlign = Paint.Align.CENTER
    }

  private val weatherNameTextPaint =
    TextPaint().apply {
      textSize = 18.dp
      textAlign = Paint.Align.CENTER
    }

  private val weatherNameBackgroundPaint =
    Paint().apply {
      style = Style.FILL
    }

  private val textBound = Rect()

  private fun drawWeatherName(
    canvas: Canvas,
    index: Int,
    point: Point,
  ) {
    val item = data[index]
    val color = colors[index]
    weatherNameTextPaint.color = color
    weatherNameBackgroundPaint.color = getColorWithAlpha(.1f, color)
    weatherNameTextPaint.getTextBounds(item.weatherName, 0, item.weatherName.length, textBound)
    canvas.drawRoundRect(
      point.x.toFloat() - itemWidth / 2f + 4.dp,
      paddingTop + topIconHeight + lineHeight + tempTextHeight + popHeight + weatherNameHeight / 2f + textBound.height() / 2f + 6.dp,
      point.x.toFloat() + itemWidth / 2f - 4.dp,
      paddingTop + topIconHeight + lineHeight + tempTextHeight + popHeight + weatherNameHeight / 2f - textBound.height() / 2f - 4.dp,
      12.dp,
      12.dp,
      weatherNameBackgroundPaint,
    )
    canvas.drawText(
      item.weatherName,
      point.x.toFloat(),
      paddingTop + topIconHeight + lineHeight + tempTextHeight + popHeight + weatherNameHeight / 2f + textBound.height() / 2f,
      weatherNameTextPaint,
    )
  }

  private val drawTimeTextPaint =
    TextPaint().apply {
      textSize = 16.dp
      textAlign = Paint.Align.CENTER
      color = ContextCompat.getColor(context, R.color.text_color_white)
      typeface = Typeface.DEFAULT_BOLD
    }

  private val sdfHHMM = SimpleDateFormat("HH", Locale.CHINA)

  private fun drawTime(
    canvas: Canvas,
    point: Point,
    index: Int,
  ) {
    val time = sdfHHMM.format(data[index].fxTime())
    if ((time.toIntOrNull() ?: 0) <= 12) {
      drawTimeTextPaint.getTextBounds("$time AM", 0, time.length, textBound)
      canvas.drawText(
        "${time} AM",
        point.x.toFloat(),
        height - (timeHeight - textBound.height()) / 2f,
        drawTimeTextPaint,
      )
    } else {
      drawTimeTextPaint.getTextBounds("$time PM", 0, time.length, textBound)
      canvas.drawText(
        "$time PM",
        point.x.toFloat(),
        height - (timeHeight - textBound.height()) / 2f,
        drawTimeTextPaint,
      )
    }

  }

  private fun drawPop(
    canvas: Canvas,
    index: Int,
    point: Point,
  ) {
    if (data[index].pop > 40) {
      popTextPaint.color = ContextCompat.getColor(context, R.color.text_color_white)
      popTextPaint.typeface = Typeface.DEFAULT_BOLD
    } else {
      popTextPaint.color = ContextCompat.getColor(context, R.color.text_color_white)
      popTextPaint.typeface = Typeface.DEFAULT
    }
    canvas.drawText(
      "${data[index].pop}%",
      point.x.toFloat(),
      paddingTop + topIconHeight + lineHeight + tempTextHeight + popHeight / 2f + 12.dp,
      popTextPaint,
    )
  }

  private fun drawTempText(
    canvas: Canvas,
    index: Int,
    point: Point,
  ) {
    canvas.drawText(
      "${data[index].temp}℃",
      point.x.toFloat(),
      point.y - 20.dp,
      tempPaint,
    )
  }

  private fun drawIcon(
    canvas: Canvas,
    index: Int,
    point: Point,
  ) {
    val bitmap = bitmaps[index]
    canvas.drawBitmap(
      bitmap,
      point.x - bitmap.width / 2f,
      paddingTop * 1f,
      topIconPaint,
    )
  }

  private fun drawTempLine(canvas: Canvas) {
    canvas.drawPath(tempLineBgPath, lineBgPaint)
    canvas.drawPath(tempLinePath, linePaint)
  }

  private val rainRectPaint =
    Paint().apply {
      strokeWidth = itemWidth / 5f
      color = ContextCompat.getColor(context, R.color.rain_pop)
    }

  private fun drawRainLine(canvas: Canvas) {
    for (point in rainPointList) {
      canvas.drawRoundRect(
        point.x.toFloat() - itemWidth / 8f,
        point.y.toFloat(),
        point.x.toFloat() + itemWidth / 8f,
        paddingTop + 12.dp + 24.dp +
            tempTextHeight + 12.dp +
            lineHeight - 12.dp,
        14f, 14f,
        rainRectPaint
      )
//      canvas.drawLine(
//        point.x.toFloat(),
//        point.y.toFloat(),
//        point.x.toFloat(),
//        paddingTop + 12.dp + 24.dp +
//            tempTextHeight + 12.dp +
//            lineHeight - 12.dp,
//        rainRectPaint,
//      )
    }
  }

  private fun drawBaseLine(canvas: Canvas) {
    canvas.drawLine(
      paddingLeft.toFloat(),
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp,
      paddingLeft + paddingRight + mPointList.size * itemWidth,
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp,
      baseLinePaint,
    )
    canvas.drawLine(
      paddingLeft.toFloat(),
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp - lineHeight,
      paddingLeft + paddingRight + mPointList.size * itemWidth,
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp - lineHeight,
      baseLinePaint,
    )
    canvas.drawLine(
      paddingLeft.toFloat(),
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp - lineHeight / 2f,
      paddingLeft + paddingRight + mPointList.size * itemWidth,
      paddingTop + 12.dp + 24.dp +
          tempTextHeight + 12.dp +
          lineHeight - 12.dp - lineHeight / 2f,
      baseLinePaint,
    )
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(
        (paddingTop + paddingBottom + topIconHeight + tempTextHeight + lineHeight + popHeight + weatherNameHeight + timeHeight)
          .toInt(),
        heightMeasureSpec,
      ),
    )
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    mVelocityTracker.addMovement(event)
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        lastX = event.x
        lastY = event.y
        if (!mScroller.isFinished) {
          mScroller.abortAnimation()
        }
        mVelocityTracker.clear()
        mVelocityTracker.addMovement(event)
      }

      MotionEvent.ACTION_MOVE -> {
        val x = event.x
        val y = event.y
        offset += lastX - x
        lastX = x
        lastY = y
        postInvalidateOnAnimation()
      }

      MotionEvent.ACTION_UP -> {
        mVelocityTracker.computeCurrentVelocity(1200, maximumFlingVelocity.toFloat())
        val xVelocity = mVelocityTracker.xVelocity.toInt()
        if (Math.abs(xVelocity) > minimumFlingVelocity) {
          mScroller.fling(
            offset.toInt(),
            0,
            -xVelocity / 2,
            0,
            0,
            (paddingLeft + paddingRight + mPointList.size * itemWidth - width).toInt(),
            0,
            0,
          )
          invalidate()
        } else {
          val des = (Math.round(offset / gapWidth) * gapWidth).toInt()
          mScroller.startScroll(offset.toInt(), 0, (des - offset).toInt(), 0)
          invalidate()
        }
      }
    }
    return true
  }

  private var initX = 0f

  private var initY = 0f

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    var dealtX = 0f
    var dealtY = 0f
    val parentPager = getViewPager2Parent(this.parent)
    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        parentPager?.isUserInputEnabled = false
        parent.requestDisallowInterceptTouchEvent(true)
        initX = ev.x
        initY = ev.y
      }

      MotionEvent.ACTION_MOVE -> {
        dealtX += abs(ev.x - initX).toInt()
        dealtY += abs(ev.y - initY).toInt()
        if (dealtX >= dealtY) {
          parent.requestDisallowInterceptTouchEvent(true)
        } else {
          parent.requestDisallowInterceptTouchEvent(false)
        }
      }

      MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
        parentPager?.isUserInputEnabled = true
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  private var parentPager2: ViewPager2? = null

  private fun getViewPager2Parent(view: ViewParent?): ViewPager2? {
    if (parentPager2 != null) return parentPager2
    if (view == null) return null
    if (view is ViewPager2) {
      parentPager2 = view
      return parentPager2
    }
    return getViewPager2Parent(view.parent)
  }

  override fun computeScroll() {
    if (mScroller.computeScrollOffset()) {
      offset = mScroller.currX * 1f
      postInvalidateOnAnimation()
    }
  }
}
