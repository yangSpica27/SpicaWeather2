package me.spica.spicaweather2.view.line

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.HorizontalScrollView
import kotlin.math.abs

class IndexHorizontalScrollView : HorizontalScrollView {


  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private val hourlyForecastView: HourlyForecastView by lazy {
    getChildAt(0) as HourlyForecastView
  }



  @SuppressLint("SuspiciousIndentation")
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val offset = computeHorizontalScrollOffset()
    val maxOffset: Int = computeHorizontalScrollRange() - getScreenWidth(context)
      hourlyForecastView.setScrollOffset(offset, maxOffset)
  }

  // 手的移动要大于这个距离才开始移动控件
  private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

  private var mPointerId = 0

  private var mInitialX = 0f

  private var mInitialY = 0f


  private var mBeingDragged = false

  private var mHorizontalDragged = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(ev: MotionEvent): Boolean {
    when (ev.actionMasked) {
      MotionEvent.ACTION_POINTER_DOWN -> {
        val index: Int = ev.actionIndex
        mPointerId = ev.getPointerId(index)
        mInitialX = ev.getX(index)
        mInitialY = ev.getY(index)
      }

      MotionEvent.ACTION_MOVE -> {
        val index: Int = ev.findPointerIndex(mPointerId)
        if (index != -1) {
          val x: Float = ev.getX(index)
          val y: Float = ev.getY(index)
          if (!mBeingDragged && !mHorizontalDragged) {
            mBeingDragged = true
            if (abs(x - mInitialX) > abs(y - mInitialY)) {
              mHorizontalDragged = true
            } else {
              parent.requestDisallowInterceptTouchEvent(false)
            }
          }
        }

      }
      MotionEvent.ACTION_POINTER_UP -> {
        val index: Int = ev.getActionIndex()
        val id: Int = ev.getPointerId(index)
        if (mPointerId == id) {
          val newIndex = if (index == 0) 1 else 0
          mPointerId = ev.getPointerId(newIndex)
          mInitialX = ev.getX(newIndex)
          mInitialY = ev.getY(newIndex)
        }
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        mBeingDragged = false
        mHorizontalDragged = false
        parent.requestDisallowInterceptTouchEvent(false)
      }
    }

    return super.onTouchEvent(ev)
  }

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    when (ev.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        mBeingDragged = false
        mHorizontalDragged = false
        mPointerId = ev.getPointerId(0)
        mInitialX = ev.x
        mInitialY = ev.y
        parent.requestDisallowInterceptTouchEvent(true)
      }
      MotionEvent.ACTION_POINTER_DOWN -> {
        val index: Int = ev.actionIndex
        mPointerId = ev.getPointerId(index)
        mInitialX = ev.getX(index)
        mInitialY = ev.getY(index)
      }
      MotionEvent.ACTION_MOVE -> {
        val index: Int = ev.findPointerIndex(mPointerId)
        if (index != -1) {
          val x: Float = ev.getX(index)
          val y: Float = ev.getY(index)
          if (!mBeingDragged && !mHorizontalDragged) {
            if (abs(x - mInitialX) > mTouchSlop || abs(y - mInitialY) > mTouchSlop) {
              mBeingDragged = true
              if (Math.abs(x - mInitialX) > abs(y - mInitialY)) {
                mHorizontalDragged = true
              } else {
                parent.requestDisallowInterceptTouchEvent(false)
              }
            }
          }
        }

      }
      MotionEvent.ACTION_POINTER_UP -> {
        val index: Int = ev.actionIndex
        val id: Int = ev.getPointerId(index)
        if (mPointerId == id) {
          val newIndex = if (index == 0) 1 else 0
          mPointerId = ev.getPointerId(newIndex)
          mInitialX = ev.getX(newIndex)
          mInitialY = ev.getY(newIndex)
        }
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        mBeingDragged = false
        mHorizontalDragged = false
        parent.requestDisallowInterceptTouchEvent(false)
      }
    }

    return super.onInterceptTouchEvent(ev) && mBeingDragged && mHorizontalDragged
  }

  private fun getScreenWidth(context: Context): Int {
    val dm = context.resources.displayMetrics
    return dm.widthPixels
  }

}