package me.spica.spicaweather2.view

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.tools.locationOnScreen
import me.spica.spicaweather2.view.Manger2HomeView.Companion.mBackground

class CardToDetailView(context: Context) : View(context) {


    private val mRootView: ViewGroup by lazy {
        return@lazy (getActivityFromContext(context).window.decorView as ViewGroup)
    }


    private val crossAnim = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 400
        addUpdateListener {
            postInvalidateOnAnimation()
        }
        doOnEnd {
            detachFromRootView()
        }
    }

    companion object {
        private var originRect = Rect()
        private var mBackgroundBitmap: Bitmap? = null
        private var mUIBackgroundBitmap: Bitmap? = null
        fun init(from: View) {
            from.locationOnScreen(rectBuffer = originRect)
        }
    }

    private val bitmapPaint = Paint( ).apply {
        isDither = true
    }

    private val rectPaint = Paint( ).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmapPaint.alpha = 255
        mBackground?.let {
            canvas.drawBitmap(it, 0f, 0f, bitmapPaint)
        }
        bitmapPaint.alpha = (255 * crossAnim.animatedValue as Float).toInt()
        mUIBackgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, bitmapPaint)
        }
        val progress = crossAnim.animatedValue as Float
        canvas.drawRoundRect(
            originRect.left - originRect.left * progress,
            originRect.top - originRect.top * progress,
            originRect.right + (width - originRect.right) * progress,
            originRect.bottom + (height - originRect.bottom) * progress,
            8.dp,
            8.dp,
            rectPaint
        )
    }

    fun attachToRootView(root: ViewGroup) {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mRootView.addView(this)
    }

    fun detachFromRootView() {
        mRootView.removeView(this)
        if (mBackgroundBitmap?.isRecycled == false) {
            mBackgroundBitmap?.recycle()
            mBackgroundBitmap = null
        }
        if (mUIBackgroundBitmap?.isRecycled == false) {
            mUIBackgroundBitmap?.recycle()
            mUIBackgroundBitmap = null
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


}