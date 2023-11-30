package me.spica.spicaweather2.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import me.spica.spicaweather2.tools.dp


class Manger2HomeView : View {

    companion object {
        var mBackground: Bitmap? = null
        var originRect = RectF()
    }

    private var clearRect = RectF(0f, 0f, 0f, 0f)

    private var clearPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mRootView: ViewGroup by lazy {
        return@lazy (getActivityFromContext(context).window.decorView as ViewGroup)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val progressAnimation =
        ValueAnimator.ofFloat(0f, 1f).setDuration(550).apply {
            interpolator = DecelerateInterpolator(1.5f)
            addUpdateListener {
                val progress = it.animatedValue as Float
                val widthExtra = (width - originRect.width()) / 2f * progress
                val topExtra = (originRect.top) * progress
                val bottomExtra = (height - originRect.bottom) * progress
                clearRect.set(
                    originRect.left - widthExtra,
                    originRect.top - topExtra,
                    originRect.right + widthExtra,
                    originRect.bottom + bottomExtra
                )
                postInvalidateOnAnimation()
            }
            doOnEnd {
                detachFromRootView()
                clearRect.set(0f, 0f, 0f, 0f)
            }
        }

    private fun getActivityFromContext(context: Context): Activity {
        var mContext: Context? = context
        while (mContext is ContextWrapper) {
            if (mContext is Activity) {
                return mContext
            }
            mContext = mContext.baseContext
        }
        throw RuntimeException("Activity not found!")
    }


    fun startAnim() {
        progressAnimation.start()
    }

    var isAttached = false
        private set

    fun attachToRootView() {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mRootView.addView(this)
        isAttached = true
        invalidate()
    }


    private fun detachFromRootView() {
        mRootView.removeView(this)
        if (mBackground != null) {
            if (mBackground?.isRecycled == false) {
                mBackground?.recycle()
            }
            mBackground = null
        }
        isAttached = false
    }

    private val clearXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val layer: Int = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        clearPaint.xfermode = null
        mBackground?.let { canvas.drawBitmap(it, 0f, 0f, clearPaint) }
        clearPaint.xfermode = clearXfermode
        canvas.drawRoundRect(
            clearRect,
            12.dp,
            12.dp,
            clearPaint
        )
        canvas.restoreToCount(layer)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }


}