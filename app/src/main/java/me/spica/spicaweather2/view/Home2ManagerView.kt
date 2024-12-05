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
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.spica.spicaweather2.tools.BlurBitmapUtil
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.ui.main.ActivityMain
import timber.log.Timber

class Home2ManagerView : View {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  // 结束的View的位置
  private val endRect: RectF = RectF(0f, 0f, 0f, 0f)

  // 标记是否已经绑定了结束的View
  @Volatile
  private var hasBind = false

  // 结束的View的Bitmap
  private var toViewBitmap: Bitmap? = null

  // 背景的Bitmap
  private var bgBitmap: Bitmap? = null

  // 模糊过的bgBitmap
  private var blurBgBitmap: Bitmap? = null

  private val blurHandlerThread = HandlerThread("blur-thread").apply { start() }

  private val blurHandler = Handler(blurHandlerThread.looper)

  private val lock = Any()

  private val blurRunnable =
    Runnable {
      while (!Thread.interrupted()) {
        if (bgBitmap == null || bgBitmap?.isRecycled == true) continue
        if (!progressAnimation.isRunning) continue
        val blurBitmap: Bitmap? =
          BlurBitmapUtil.blurBitmap(
            context,
            bgBitmap!!,
            (25 - (progressAnimation.animatedValue as Float) * 25),
          )
        synchronized(lock) {
          if (blurBitmap?.isRecycled == false) {
            blurBgBitmap?.recycle()
          }
          if (blurBitmap != null) {
            blurBgBitmap = blurBitmap
          }
        }
        try {
          Thread.sleep(16)
        } catch (e: InterruptedException) {
          break
        }
      }
    }

  fun bindEndView(
    view: View,
    parent: ViewGroup,
  ) {
    if (hasBind) return
    CoroutineScope(Dispatchers.Default).launch {
      val intArray = IntArray(2)
      view.getLocationInWindow(intArray)
      mPaint.color = ActivityMain.currentThemeColor
      endRect.set(
        intArray[0] * 1f,
        intArray[1] * 1f,
        intArray[0] + view.width * 1f,
        intArray[1] + view.height * 1f,
      )
      toViewBitmap = view.drawToBitmap()
      view.alpha = 0f
      bgBitmap = parent.drawToBitmap()
      blurBgBitmap = BlurBitmapUtil.blurBitmap(context, bgBitmap!!, 25f)
      view.alpha = 1f
      hasBind = true
      blurHandler.post(blurRunnable)
      if (needStartAnimWhenBindOk) {
        startAnim()
        needStartAnimWhenBindOk = false
      }
    }
  }

  private var endAction: (() -> Unit)? = null

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
    ValueAnimator.ofFloat(0f, 1f).setDuration(325).apply {
      interpolator = DecelerateInterpolator(1.5f)
      addUpdateListener {
        val progress = it.animatedValue as Float

        // 设置绘制区域
        drawRect.set(
          0f + (endRect.left) * progress,
          0f + (endRect.top) * progress,
          width * 1f - (width - endRect.right) * progress,
          height * 1f - (height - endRect.bottom) * progress,
        )

        if (drawRect.bottom > lastB) {
          Timber
            .tag("异常数据")
            .e("height:$height bottom:${drawRect.bottom} lastB:$lastB progress:$progress")
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
    layoutParams =
      FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
      )
    if (parent == null) {
      mRootView.addView(this)
    }
//        setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, 0x80))
    progressAnimation.cancel()
    isAttached = true
    invalidate()
  }

  // 标记，防止初始化数据未就绪时，动画开始
  @Volatile
  private var needStartAnimWhenBindOk = false

  fun startAnim() {
    if (!hasBind) {
      // 未初始化完成，不执行动画，等执行完毕后再执行
      needStartAnimWhenBindOk = true
      return
    }
    if (progressAnimation.isRunning) return
    post { progressAnimation.start() }
    Timber.tag("动画").e("开始")
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (progressAnimation.isRunning) progressAnimation.cancel()
    if (toViewBitmap?.isRecycled == false) {
      toViewBitmap?.recycle()
    }
    blurHandler.removeCallbacks(blurRunnable)
    blurHandlerThread.interrupt()
    blurHandlerThread.join(16)
    blurHandlerThread.quitSafely()
    if (bgBitmap?.isRecycled == false) {
      bgBitmap?.recycle()
    }
    if (blurBgBitmap?.isRecycled == false) {
      blurBgBitmap?.recycle()
    }
  }

  private var isAttached = false

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

  private val markerPaint =
    Paint().apply {
      style = Paint.Style.FILL
    }

  private val bgBitmapPaint = Paint()

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    mPaint.alpha = 255
    // 如果动画未开始，直接绘制上一页面的bitmap作为占位防止闪烁
    if (!progressAnimation.isRunning) {
      if (ActivityMain.screenBitmap == null) return
      canvas.drawBitmap(ActivityMain.screenBitmap!!, 0f, 0f, mPaint)
      return
    }

    // 绘制从模糊到清晰的背景
    if (blurBgBitmap != null && bgBitmap != null) {
      synchronized(lock) {
        canvas.drawBitmap(blurBgBitmap!!, 0f, 0f, bgBitmapPaint)
      }
    }

    // 保存图层
    val layer: Int = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
    // 绘制背景
    mPaint.xfermode = null
    // 圈定保留区域
    canvas.drawRoundRect(
      drawRect,
      8.dp * progressAnimation.animatedFraction,
      8.dp * progressAnimation.animatedFraction,
      mPaint,
    )
    // 绘制过渡区域内容
    mPaint.xfermode = srcInXfermode
    if (ActivityMain.screenBitmap != null && progressAnimation.animatedFraction < .6f) {
      canvas.drawBitmap(ActivityMain.screenBitmap!!, 0f, 0f, mPaint)
    } else {
      canvas.drawRect(0f, 0f, width * 1f, height * 1f, mPaint)
    }

    // 恢复图层
    canvas.restoreToCount(layer)
    mPaint.xfermode = null

    markerPaint.color =
      ColorUtils.setAlphaComponent(
        ActivityMain.currentThemeColor,
        (progressAnimation.animatedFraction * 255).toInt(),
      )
    canvas.drawRoundRect(
      drawRect,
      8.dp * progressAnimation.animatedFraction,
      8.dp * progressAnimation.animatedFraction,
      markerPaint,
    )

    // 动画进度到90%时，渐显绘制下一页面的被遮挡的内容
    if (toViewBitmap != null && progressAnimation.animatedFraction > .9f) {
      mPaint.alpha = (255 * ((progressAnimation.animatedFraction - 0.9f) / (0.1))).toInt()
      canvas.drawBitmap(toViewBitmap!!, endRect.left, endRect.top, mPaint)
    }
//    canvas.drawBitmap(bgBitmap!!, 0f, 0f, mPaint)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean = progressAnimation.animatedFraction != 1f
}
