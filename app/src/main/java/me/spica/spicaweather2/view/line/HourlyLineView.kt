package me.spica.spicaweather2.view.line

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import kotlin.math.abs


class HourlyLineView : View {


    private val mScroller: OverScroller = OverScroller(context)
    private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain();
    private val maximumFlingVelocity =
        ViewConfiguration.get(context).scaledMaximumFlingVelocity;
    private val minimumFlingVelocity =
        ViewConfiguration.get(context).scaledMinimumFlingVelocity;


    // 手的移动要大于这个距离才开始移动控件
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private val gapWidth = 50f
    private var offset = 0f
    private var lastX = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mVelocityTracker.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
                mVelocityTracker.clear()
                mVelocityTracker.addMovement(event)
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                if (abs(event.x - lastX) > mTouchSlop || abs(event.y - lastX) > mTouchSlop) {
                    return false
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                val x = event.x
                offset += lastX - x
                lastX = x
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                mVelocityTracker.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                val xVelocity = mVelocityTracker.xVelocity.toInt()
                if (Math.abs(xVelocity) > minimumFlingVelocity) {
                    mScroller.fling(
                        offset.toInt(),
                        0,
                        -xVelocity / 2,
                        0,
                        Int.MIN_VALUE,
                        Int.MAX_VALUE,
                        0,
                        0
                    )
                    invalidate()
                } else {
                    val des = (Math.round(offset / gapWidth) * gapWidth).toInt()
                    mScroller.startScroll(offset.toInt(), 0, (des - offset).toInt(), 0)
                    invalidate()
                }
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (!mScroller.computeScrollOffset()) {
                val des = (Math.round(offset / gapWidth) * gapWidth.toInt())
                mScroller.startScroll(offset.toInt(), 0, (des - offset).toInt(), 0)
            }
            offset = mScroller.currX * 1f
            invalidate()
        }
    }


}