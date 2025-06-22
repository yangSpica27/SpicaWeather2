package me.spica.spicaweather2.view.scroller_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import timber.log.Timber

/**
 * 处理了滑动冲突的滑动布局
 */
abstract class ScrollViewAtViewPager : ScrollView {
  private var startX = 0

  private var startY = 0



//  private var pullDownFraction = 0f
//
//
//  private val objectAnimator = ObjectAnimator.ofFloat(this, "pullDownFraction", 0f, 0f).apply {
//    duration = 350
//  }
//
//
//  @Keep
//  private fun setPullDownFraction(
//    pullDownFraction: Float,
//  ) {
//    this.pullDownFraction = pullDownFraction
//    postInvalidateOnAnimation()
//  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        startX = ev.x.toInt()
        startY = ev.y.toInt()
        parent.requestDisallowInterceptTouchEvent(true)
      }

      MotionEvent.ACTION_MOVE -> {
        val endX = ev.x.toInt()
        val endY = ev.y.toInt()
        val disX = Math.abs(endX - startX)
        val disY = Math.abs(endY - startY)
        if (disX > disY) {
          // 如果是纵向滑动，告知父布局不进行时间拦截，交由子布局消费，　requestDisallowInterceptTouchEvent(true)
          parent.requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX))
        } else {
          parent.requestDisallowInterceptTouchEvent(canScrollVertically(startX - endX))
        }
//        if (!canScrollVertically(-1) && !objectAnimator.isRunning) {
//          setPullDownFraction(((ev.y - startY) / (height / 4f)).coerceIn(0f, 1f))
//        }
      }

      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//        objectAnimator.setFloatValues(
//          pullDownFraction,
//          0f
//        )
//        objectAnimator.start()
        parent.requestDisallowInterceptTouchEvent(false)
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
  }

  private val pointPaint = Paint().apply {
    color = android.graphics.Color.WHITE
    strokeCap = android.graphics.Paint.Cap.ROUND
  }


  override fun onDrawForeground(canvas: Canvas) {
    super.onDrawForeground(canvas)
//    canvas.withTranslation(
//      width / 2f,
//      height / 4f * pullDownFraction - 45.dp
//    ) {
////      pointPaint.alpha = (255 * pullDownFraction).toInt()
//      pointPaint.strokeWidth = 55.dp
//      pointPaint.color = ContextCompat.getColor(context, R.color.white)
//      canvas.drawPoint(0f, 0f, pointPaint)
//      pointPaint.strokeWidth = 35.dp - 35.dp * pullDownFraction + 20.dp
//      pointPaint.color = ContextCompat.getColor(context, R.color.black)
//      canvas.drawPoint(0f, 0f, pointPaint)
//    }
  }



  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

}
