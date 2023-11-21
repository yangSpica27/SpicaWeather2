package me.spica.spicaweather2.view.line

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.WeatherCodeUtils
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.HourlyWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.tools.getColorWithAlpha
import java.text.SimpleDateFormat
import java.util.Locale

// 宽度75dp 高度200dp

class HourlyForecastView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val weathers: ArrayList<HourlyWeatherBean> = arrayListOf() // 数据源


    private var themeColor = ContextCompat.getColor(context, R.color.black)

    private var fraction = 1f

    private val showInAnim = ObjectAnimator.ofFloat(1f, 0f)
        .apply {
            interpolator = OvershootInterpolator()
            addUpdateListener {
                if (visibility == View.INVISIBLE) {
                    visibility = VISIBLE
                }
                fraction = this.animatedValue as Float
                // 计算绘制的折线图Y轴坐标 并且重绘 实现从下向上浮动效果
                init()
            }
            duration = 850
        }


    fun startAnim(delay: Long) {
        if (showInAnim.isRunning) {
            showInAnim.cancel()
        }
        showInAnim.startDelay = delay
        showInAnim.start()
    }

    fun setData(weather: Weather) {
        this.weathers.clear()
        this.weathers.addAll(weather.hourlyWeather)
        themeColor = WeatherCodeUtils.getWeatherCode(weather.todayWeather.iconId).getThemeColor()
        levelPaint.color = getColorWithAlpha(.2f, themeColor)
        init()
    }

    private fun init(isAnim: Boolean = false) {
        // 获取最高气温和最低气温
        val sorts = weathers.toList().sortedBy { it.temp }
        if (sorts.isEmpty())return
        minTemp = sorts.first().temp
        maxTemp = sorts.last().temp

        tempLinePath.reset()
        shadowPath.reset()

        dashLineList.clear()
        mPointList.clear()

        weathers.forEachIndexed { index, hourlyWeatherBean ->
            // 横坐标
            val w: Float = (ITEM_WIDTH * index + paddingL)
            // 纵坐标
            val h: Float = tempHeightPixel(hourlyWeatherBean.temp) + fraction *
                (ITEM_MIN_HEIGHT + paddingT + paddingB + DATE_TEXT_HEIGHT)
            // 当前点
            val point = Point(w.toInt(), h.toInt())
            mPointList.add(point)

            if (index != 0 &&
                hourlyWeatherBean.getWeatherType() !=
                weathers[index - 1].getWeatherType()
            ) {
                if (index != weathers.size - 1) {
                    dashLineList.add(index)
                }
            }


            if (dashLineList.contains(index)) {
                dashWidth.add(w)
                dashHeight.add(h)
            }
            if (index == 0) {
                // 使用当前的做线段的主题色
                tempLinePaint.color = themeColor
                dashLinePaint.color = themeColor
                shadowPaint.shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    paddingT + ITEM_MIN_HEIGHT + TEMP_TEXT_HEIGHT + TEMP_HEIGHT_SECTION,
                    getColorWithAlpha(.4f, themeColor),
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            }
        }
        weathers.forEachIndexed { index, _ ->

            val point = mPointList[index]

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
                    shadowPath.moveTo(point.x * 1f, point.y * 1f)

                }

                1 -> {
                    lastPoint = Point(point.x, point.y)
                    lastLastPoint = Point(point.x, point.y)
                    nextPoint = Point(mPointList[index + 1].x, mPointList[index + 1].y)
                }

                weathers.size - 1 -> {
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
                val controlPointLeft = PointF(
                    lastPoint.x + 0.1f * firstDiffX, lastPoint.y + 0.1f * firstDiffY
                )
                val controlPointRight = PointF(
                    point.x - 0.1f * secondDiffX, point.y - 0.1f * secondDiffY
                )
                // 做二阶贝塞尔
                tempLinePath.cubicTo(
                    controlPointLeft.x, controlPointLeft.y, controlPointRight.x, controlPointRight.y, point.x * 1f, point.y * 1f
                )
                shadowPath.cubicTo(
                    controlPointLeft.x, controlPointLeft.y, controlPointRight.x, controlPointRight.y, point.x * 1f, point.y * 1f
                )

            }
        }

        shadowPath.lineTo(
            mPointList.last().x * 1f, paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + TEMP_TEXT_HEIGHT
        )

        shadowPath.lineTo(
            paddingL, paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + TEMP_TEXT_HEIGHT
        )

        shadowPath.close()

        if (isAnim) {
            postInvalidateOnAnimation()
            return
        }
        requestLayout()
        postInvalidate()
    }

    private var minTemp = 0

    private var maxTemp = 0


    private val screenWidth by lazy {
        val dm = resources.displayMetrics
        dm.widthPixels
    }

    companion object {
        private val ITEM_WIDTH = 75.dp // 每个单元的宽度

        private val ITEM_MIN_HEIGHT = 35.dp // 每个单元最低的高度

        private val TEMP_TEXT_HEIGHT = 24.dp // 预留给温度指示器的间距

        private val DATE_TEXT_HEIGHT = 20.dp // 日期文本预留空间

        private val LEVEL_RECT_HEIGHT = 18.dp // 风力等级治时期高度

        private val TEMP_HEIGHT_SECTION = 25.dp // 温度折线图高度可变区间

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            (ITEM_WIDTH * (weathers.size - 1) + paddingL + paddingR).toInt(),
            (ITEM_MIN_HEIGHT + paddingT + paddingB + DATE_TEXT_HEIGHT +
                    LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT + TEMP_HEIGHT_SECTION).toInt()
        )
    }

    private val ditherPaint = Paint().apply {
        isDither = true
        color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDate(canvas)
        drawLines(canvas)
        drawPops(canvas)
        drawTemp(canvas)
    }

    private val tempTextRect = Rect()

    private val tempTextPaint = TextPaint().apply {
        textSize = 12.dp
        color = context.getColor(R.color.window_background)
    }
    private val tempTextBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private fun drawTemp(canvas: Canvas) {
        if (weathers.isEmpty())return
        val item = weathers[currentItemIndex]
        tempTextBgPaint.color = item.getWeatherType().getThemeColor()
        val showText = "${item.weatherName},${item.temp}℃"
        val y = (tempHeightPixel(item.temp)).toInt()
        val offset = ITEM_WIDTH / 4f
        tempTextRect.left = getScrollBarX().toInt()
        tempTextRect.top = (y - TEMP_TEXT_HEIGHT).toInt()
        tempTextRect.right = (getScrollBarX() + offset).toInt()
        tempTextRect.bottom = (y - TEMP_TEXT_HEIGHT + 20.dp).toInt()

        val fontMetrics: FontMetricsInt = tempTextPaint.fontMetricsInt
        val baseline: Int = (tempTextRect.bottom + tempTextRect.top - fontMetrics.bottom - fontMetrics.top) / 2

        tempTextPaint.textAlign = Paint.Align.LEFT
        dateTextPaint.getTextBounds(showText, 0, showText.toCharArray().size, textRect)

        canvas.drawRoundRect(
            tempTextRect.centerX() - 4.dp,
            tempTextRect.top * 1f,
            tempTextRect.centerX() + textRect.width().toFloat(),
            tempTextRect.bottom * 1f, 3.dp, 3.dp, tempTextBgPaint
        )

        tempTextBgPaint.color = getColorWithAlpha(.1f, tempTextBgPaint.color)

        canvas.drawRoundRect(
            tempTextRect.centerX() - ITEM_WIDTH / 2f + textRect.width() / 2f,
            paddingT / 2f,
            tempTextRect.centerX() + ITEM_WIDTH / 2f + textRect.width() / 2f,
            height * 1f - paddingB / 2f,
            4.dp,
            4.dp,
            tempTextBgPaint
        )

        canvas.drawText(showText, tempTextRect.centerX().toFloat(), baseline.toFloat(), tempTextPaint)

    }

    private val dateTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            textSize = 14.dp
            color = context.getColor(R.color.textColorPrimaryHint)
        }

    private val sdfHHMM = SimpleDateFormat("HH:mm", Locale.CHINA)

    private val textRect = Rect()

    // 绘制事件
    private fun drawDate(canvas: Canvas) {
        weathers.forEachIndexed { index, hourlyWeatherBean ->
            val drawText = sdfHHMM.format(hourlyWeatherBean.fxTime())
            dateTextPaint.getTextBounds(drawText, 0, drawText.toCharArray().size, textRect)
            canvas.drawText(
                drawText,
                ITEM_WIDTH * index + paddingL - textRect.width() / 2f,
                DATE_TEXT_HEIGHT + paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT,
                dateTextPaint
            )
        }
    }


    private val dashWidth: MutableList<Float> = arrayListOf()

    private val dashHeight: MutableList<Float> = arrayListOf()

    private val mPointList: MutableList<Point> = arrayListOf()

    //画虚线的点的index
    private val dashLineList: MutableList<Int> = arrayListOf()


    private val paddingL = 20.dp
    private val paddingT = 12.dp

    private val paddingR = 20.dp
    private val paddingB = 12.dp


    // 气温曲线的轨迹
    private val tempLinePath = Path()

    // 阴影背景的Path
    private val shadowPath = Path()


    // 气温折线的配套画笔
    private val tempLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 3.dp
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val dashLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(8f, 8f, 8f, 8f), 1f)
        strokeWidth = 3f
    }

    // 用于绘制矩形
    private val levelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.FILL
        }

    private val roundRect = RectF()
    private fun drawLines(canvas: Canvas) {
        // 绘制气温折线

        if (dashLineList.isEmpty()) {
            // 绘制一整个块
            roundRect.setEmpty()
            roundRect.left = paddingL + 2.dp
            roundRect.right = width - paddingR - 2.dp
            roundRect.top = ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT + 4
            roundRect.bottom = paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT + 4
            canvas.drawRoundRect(
                roundRect,
                2.dp,
                2.dp,
                levelPaint
            )
        } else {
            // 补充绘制末尾的item块
            levelPaint.color = getColorWithAlpha(.3f, weathers.last().getWeatherType().getThemeColor())
            roundRect.setEmpty()
            roundRect.left = mPointList[dashLineList.last()].x * 1f
            roundRect.right = width - paddingR - 2.dp
            roundRect.top = ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT + 4
            roundRect.bottom = paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT + 4
            canvas.drawRoundRect(
                roundRect,
                2.dp,
                2.dp,
                levelPaint
            )
        }

        dashLineList.forEachIndexed { index, i ->


            canvas.drawLine(
                mPointList[i].x * 1f,
                mPointList[i].y * 1f,
                mPointList[i].x * 1f,
                paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + TEMP_TEXT_HEIGHT,
                dashLinePaint
            )

            levelPaint.color = getColorWithAlpha(.3f, weathers[i - 1].getWeatherType().getThemeColor())
            // 绘制下方的色块
            roundRect.setEmpty()

            roundRect.left = if (index == 0) {
                paddingL + 2.dp
            } else {
                mPointList[dashLineList[index - 1]].x * 1f + 2.dp
            }

            roundRect.right = mPointList[i].x * 1f - 2.dp

            roundRect.top = ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT + 4
            roundRect.bottom = paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT + 4

            canvas.drawRoundRect(
                roundRect,
                2.dp,
                2.dp,
                levelPaint
            )
        }

        canvas.drawPath(shadowPath, shadowPaint)

        // 保存部分的已绘制内容 如坐标等
        val layerId = canvas.saveLayer(0f, 0f, width * 1f, height * 1f, null)

        // 绘制折线图
        canvas.drawPath(tempLinePath, tempLinePaint)

        // 根据fraction 按比例从右向左擦除掉折线图
        canvas.drawRect(
            screenWidth * 1f + scrollOffset,
            height * 1f,
            screenWidth + scrollOffset - screenWidth * fraction,
            0f, ditherPaint
        )

        // 恢复图层
        canvas.restoreToCount(layerId)

    }


    // 计算对应温度所对应的Y轴坐标
    private fun tempHeightPixel(temp: Int): Float {
        val res: Float = (temp - minTemp) * 1f / (maxTemp - minTemp) * (TEMP_HEIGHT_SECTION) + ITEM_MIN_HEIGHT
        return ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT - res //y从上到下

    }

    private val popPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.FILL
        }

    private val popTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.textColorPrimaryLight)
        textSize = 12.dp
    }

    private fun drawPops(canvas: Canvas) {
        mPointList.forEachIndexed { index, point ->

            popPaint.color = getColorWithAlpha(weathers[index].pop * 1f / 100 / 2f, weathers[index].getWeatherType().getThemeColor())

            val showText = "${weathers[index].pop}%"
            popTextPaint.getTextBounds(showText, 0, showText.toCharArray().size, textRect)


            if ((point.x + ITEM_WIDTH) <= width) {
                canvas.drawRoundRect(
                    point.x + 2.dp,
                    ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT,
                    point.x + ITEM_WIDTH - 2.dp,
                    ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT -
                        ((weathers[index].pop) * 1f / (100f) * (TEMP_HEIGHT_SECTION + ITEM_MIN_HEIGHT)), 3.dp, 3.dp,
                    popPaint
                )
                canvas.drawText(
                    showText,
                    point.x - textRect.width() / 2f + ITEM_WIDTH / 2f,
                    ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT -
                        ((weathers[index].pop) * 1f / (100f) * (TEMP_HEIGHT_SECTION + ITEM_MIN_HEIGHT)) - 4.dp,
                    popTextPaint
                )
            }

        }
    }


    private var maxScrollOffset = 0 //滚动条最长滚动距离

    private var scrollOffset = 0 //滚动条偏移量

    private var currentItemIndex = 0 //当前滚动的位置所对应的item下标


    //设置scrollerView的滚动条的位置，通过位置计算当前的时段
    fun setScrollOffset(offset: Int, maxScrollOffset: Int) {
        this.maxScrollOffset = (maxScrollOffset + 50.dp).toInt()
        this.scrollOffset = offset
        currentItemIndex = calculateItemIndex()
        postInvalidateOnAnimation()
    }

    // 计算当前指向了哪个Item
    private fun calculateItemIndex(): Int {
        val x: Float = getScrollBarX()
        var sum: Float = (paddingL - ITEM_WIDTH / 2)
        for (i in 0 until weathers.size - 1) {
            sum += ITEM_WIDTH
            if (x < sum) {
                return i
            }
        }
        return weathers.size - 1
    }


    // 获取x轴滚动
    private fun getScrollBarX(): Float {
        var x: Float = (weathers.size - 1) * ITEM_WIDTH * scrollOffset * 1f / maxScrollOffset
        x = (x - 3.dp)
        return x
    }


}