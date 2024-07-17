package me.spica.spicaweather2.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
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
import androidx.core.graphics.ColorUtils
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.ui.main.ActivityMain
import timber.log.Timber

class Home2ManagerView : View {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private val endRect: RectF = RectF(0f, 0f, 0f, 0f)


    private var hasBind = false

    fun bindEndView(view: View) {
        if (hasBind) return
        val intArray = IntArray(2)
        view.getLocationInWindow(intArray)
        endRect.set(
            intArray[0] * 1f,
            intArray[1] * 1f,
            intArray[0] + view.width * 1f,
            intArray[1] + view.height * 1f,
        )
        Timber.tag("坐标").e("x:${intArray[0]} y:${intArray[1]}")
        mPaint.color = ActivityMain.currentThemeColor
        hasBind = true
    }


    var endAction: (() -> Unit)? = null

    private val mRootView: ViewGroup by lazy {
        return@lazy (getActivityFromContext(context).window.decorView as ViewGroup)
    }

    // 从根布局中移除
    private fun detachFromRootView() {
        mRootView.removeView(this)
        isAttached = false
    }


    private var drawRect = RectF(0f, 0f, 0f, 0f)

    private var lastB = 0f

    private var mPaint: Paint = Paint()

    private val progressAnimation =
        ValueAnimator.ofFloat(0f, 1f).setDuration(550).apply {
//            interpolator = LinearInterpolator()
            interpolator = DecelerateInterpolator(2f)
            addUpdateListener {
                val progress = it.animatedValue as Float

                // 设置绘制区域
                drawRect.set(
                    0f + (endRect.left) * progress,
                    0f + (endRect.top) * progress,
                    width * 1f - (width - endRect.right) * progress,
                    height * 1f - (height - endRect.bottom) * progress
                )

                if (drawRect.bottom > lastB){
                    Timber.tag("异常数据").e("height:${height} bottom:${drawRect.bottom} lastB:${lastB} progress:${progress}")
                }
                lastB = drawRect.bottom
                postInvalidateOnAnimation()
            }
            doOnEnd {
                // 动画结束后，将自己从根布局中移除
                detachFromRootView()
                endAction?.invoke()
                endAction = null
            }
        }

    // 将自己添加到根布局中
    fun attachToRootView() {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mRootView.addView(this)
//        setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, 0x80))
        isAttached = true
        invalidate()
    }

    fun startAnim() {
        if (progressAnimation.isRunning) return
        progressAnimation.start()
        Timber.tag("动画").e("开始")
    }

    private var isAttached = false
        private set

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

    private val srcInXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!progressAnimation.isRunning) {
            if (ActivityMain.screenBitmap == null) return
            canvas.drawBitmap(ActivityMain.screenBitmap!!, 0f, 0f, mPaint)
            return
        }

//        // 保存图层
        val layer: Int = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
//        // 绘制背景
        mPaint.xfermode = null


        canvas.drawRoundRect(
            drawRect,
            8.dp * progressAnimation.animatedFraction,
            8.dp * progressAnimation.animatedFraction,
            mPaint
        )
        // 绘制清除区域
        mPaint.xfermode = srcInXfermode
        if (progressAnimation.animatedFraction > .905f) {
            mPaint.alpha =
                (255 - 200 * ((progressAnimation.animatedFraction - .905f) / .095f)).toInt()
        } else {
            mPaint.alpha = 255
        }
        if (ActivityMain.screenBitmap != null && progressAnimation.animatedFraction < .5f) {
            canvas.drawBitmap(ActivityMain.screenBitmap!!, 0f, 0f, mPaint)
        } else {
            canvas.drawRect(0f, 0f, width * 1f, height * 1f, mPaint)
        }
        // 恢复图层
        canvas.restoreToCount(layer)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }
}