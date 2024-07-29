package me.spica.spicaweather2.view.scroller_view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * 处理了滑动冲突的滑动布局
 */
abstract class ScrollViewAtViewPager : ScrollView {
    private var startX = 0

    private var startY = 0

    private var lastY = 0f

    private var pullDownListener: PullDownListener? = null

    fun setPullDownListener(pullDownListener: PullDownListener?) {
        this.pullDownListener = pullDownListener
    }

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
                if (!canScrollVertically(-1) && pullDownListener != null) {
                    pullDownListener!!.onPullDown(ev.y - lastY)
                } else {
                    lastY = ev.y
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.dispatchTouchEvent(ev)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    interface PullDownListener {
        fun onPullDown(downY: Float)

        fun onPullUp(downY: Float)
    }
}
