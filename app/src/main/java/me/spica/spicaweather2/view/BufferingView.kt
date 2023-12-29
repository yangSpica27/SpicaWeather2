package me.spica.spicaweather2.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BufferingView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val butterPaint = Paint()

    private var bufferBitmap: Bitmap? = null

    private var bufferBitmap2: Bitmap? = null

    private var bufferCanvas: Canvas? = null

    private var bufferCanvas2: Canvas? = null

    private val lock = Any()

    private var isUse1 = false

    // 缓存帧没用过的时候 标记下 让onDraw使用缓存帧1
    private var buffer2HasDraw = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        synchronized(lock) {
            if (bufferBitmap?.isRecycled == false) {
                bufferBitmap?.recycle()
                bufferBitmap = null
            }
            if (bufferBitmap2?.isRecycled == false) {
                bufferBitmap2?.recycle()
                bufferBitmap2 = null
            }
            bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bufferBitmap?.let {
                bufferCanvas = Canvas(it)
            }
            bufferBitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bufferBitmap2?.let {
                bufferCanvas2 = Canvas(it)
            }
        }
        isUse1 = false
        buffer2HasDraw = false
        postDraw()
    }

    @OptIn(DelicateCoroutinesApi::class)
    protected fun postDraw() {
        GlobalScope.launch(Dispatchers.Default) {
            synchronized(lock) {
                if (bufferBitmap == null || bufferCanvas == null) {
                    return@launch
                }
                if (bufferBitmap2 == null || bufferCanvas2 == null) {
                    return@launch
                }
                isUse1 = !isUse1
                if (isUse1) {
                    bufferCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    bufferCanvas?.let { drawBuffering(canvas = it) }
                } else {
                    bufferCanvas2?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    bufferCanvas2?.let { drawBuffering(canvas = it) }
                    buffer2HasDraw = true
                }
                postInvalidateOnAnimation()
            }

        }
    }

    protected abstract fun drawBuffering(canvas: Canvas)

    final override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Timber.tag("BufferingView").e("drawBuffer2 = $isUse1")
        if (!buffer2HasDraw) {
            if (bufferBitmap == null || bufferCanvas == null) {
                Timber.tag("BufferingView").e("bufferBitmap or bufferCanvas is null")
            }
            bufferBitmap?.let {
                canvas.drawBitmap(it, 0f, 0f, butterPaint)
            }
        } else if (!isUse1) {
            if (bufferBitmap == null || bufferCanvas == null) {
                Timber.tag("BufferingView").e("bufferBitmap or bufferCanvas is null")
            }
            bufferBitmap?.let {
                canvas.drawBitmap(it, 0f, 0f, butterPaint)
            }
        } else {
            if (bufferBitmap2 == null || bufferCanvas2 == null) {
                Timber.tag("BufferingView").e("bufferBitmap2 or bufferCanvas2 is null")
            }
            bufferBitmap2?.let {
                canvas.drawBitmap(it, 0f, 0f, butterPaint)
            }
        }
    }
}