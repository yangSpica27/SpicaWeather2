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

abstract class BufferingView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val butterPaint = Paint()

    private var bufferBitmap: Bitmap? = null

    private var bufferCanvas: Canvas? = null

    private val lock = Any()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        synchronized(lock) {
            if (bufferBitmap?.isRecycled == false) {
                bufferBitmap?.recycle()
                bufferBitmap = null
            }
            bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bufferBitmap?.let {
                bufferCanvas = Canvas(it)
            }
        }
        postDraw()
    }

    @OptIn(DelicateCoroutinesApi::class)
    protected fun postDraw() {
        GlobalScope.launch(Dispatchers.Default) {
            synchronized(lock) {
                if (bufferBitmap == null || bufferCanvas == null) {
                    return@launch
                }
                bufferCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                bufferCanvas?.let { drawBuffering(canvas = it) }
            }
            postInvalidateOnAnimation()
        }
    }

    protected abstract fun drawBuffering(canvas: Canvas)

    final override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            bufferBitmap?.let {
                canvas.drawBitmap(it, 0f, 0f, butterPaint)
            }
        }
    }
}