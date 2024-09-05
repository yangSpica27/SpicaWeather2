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
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.core.view.drawToBitmap
import me.spica.spicaweather2.tools.dp
import timber.log.Timber
import kotlin.math.max

class Manager2HomeView : View {
    companion object {
        // 用于保存背景图
        var mBackground: Bitmap? = null

        // 用于保存起始位置
        var originRect = RectF()

        private var themeColor = 0

        fun initFromViewRect(
            from: View,
            window: Window,
            themeColor: Int,
        ) {
            val intArray = IntArray(2)
            from.getLocationInWindow(intArray)
            this.themeColor = themeColor
            originRect.set(
                intArray[0] * 1f,
                intArray[1] * 1f,
                intArray[0] + from.width * 1f,
                intArray[1] + from.height * 1f,
            )
            try {
                mBackground = window.decorView.drawToBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var clearRect = RectF(0f, 0f, 0f, 0f)

    private var clearPaint: Paint = Paint()

    private val mRootView: ViewGroup by lazy {
        return@lazy (getActivityFromContext(context).window.decorView as ViewGroup)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    )

    var endAction: (() -> Unit)? = null

    private val progressAnimation =
        ValueAnimator.ofFloat(0f, 1f).setDuration(350).apply {
            interpolator = DecelerateInterpolator(1.2f)
            addUpdateListener {
                val progress = it.animatedValue as Float
                val widthExtra = (width - originRect.width()) / 2f * progress
                val topExtra = (originRect.top) * progress
                val bottomExtra = (height - originRect.bottom) * progress
                // 设置清除区域
                clearRect.set(
                    originRect.left - widthExtra,
                    originRect.top - topExtra,
                    originRect.right + widthExtra,
                    originRect.bottom + bottomExtra,
                )
                postInvalidateOnAnimation()
            }
            doOnEnd {
                // 动画结束后，将自己从根布局中移除
                detachFromRootView()
                // 重置
                clearRect.set(0f, 0f, 0f, 0f)
                endAction?.invoke()
                endAction = null
                Timber.tag("回复动画").e("END")
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
        if (progressAnimation.isRunning) progressAnimation.cancel()
        progressAnimation.start()
    }

    var isAttached = false
        private set

    // 将自己添加到根布局中
    fun attachToRootView() {
        layoutParams =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        if (parent == null) {
            mRootView.addView(this)
        }
        progressAnimation.cancel()
        isAttached = true
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (progressAnimation.isRunning) progressAnimation.cancel()
    }

    // 从根布局中移除
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

    private val markerPaint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 保存图层
        val layer: Int = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        // 绘制背景
        clearPaint.xfermode = null
        mBackground?.let { canvas.drawBitmap(it, 0f, 0f, clearPaint) }
        // 绘制清除区域
        clearPaint.xfermode = clearXfermode

        canvas.drawRoundRect(
            clearRect,
            12.dp,
            12.dp,
            clearPaint,
        )
        // 恢复图层
        canvas.restoreToCount(layer)

        // 绘制遮罩
        markerPaint.color =
            ColorUtils.setAlphaComponent(
                themeColor,
                max(255 - (255 * progressAnimation.animatedFraction * 2).toInt(), 0),
            )
        canvas.drawRoundRect(
            clearRect,
            12.dp,
            12.dp,
            markerPaint,
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = progressAnimation.animatedFraction != 1f
}
