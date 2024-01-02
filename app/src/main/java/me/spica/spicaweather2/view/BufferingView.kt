package me.spica.spicaweather2.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

//abstract class BufferingView : View {
//    constructor(context: Context?) : super(context)
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//    private val butterPaint = Paint()
//
//    private var bufferBitmap: Bitmap? = null
//
//    private var bufferBitmap2: Bitmap? = null
//
//    private var bufferCanvas: Canvas? = null
//
//    private var bufferCanvas2: Canvas? = null
//
//    private val lock = Any()
//
//
//    protected var isDebug: Boolean = false
//
//    private var isUse1 = false
//
//    // 缓存帧没用过的时候 标记下 让onDraw使用缓存帧1
//    private var buffer2HasDraw = false
//
//    private val lockBuffer1 = Any()
//
//    private val lockBuffer2 = Any()
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        log("重新测量")
//        synchronized(lock) {
//            if (bufferBitmap?.isRecycled == false) {
//                bufferBitmap?.recycle()
//                bufferBitmap = null
//            }
//            if (bufferBitmap2?.isRecycled == false) {
//                bufferBitmap2?.recycle()
//                bufferBitmap2 = null
//            }
//
//            synchronized(lockBuffer1) {
//                bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//                bufferBitmap?.let {
//                    bufferCanvas = Canvas(it)
//                }
//            }
//
//            synchronized(lockBuffer2) {
//                bufferBitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//                bufferBitmap2?.let {
//                    bufferCanvas2 = Canvas(it)
//                }
//            }
//        }
//        isUse1 = false
//        buffer2HasDraw = false
//        postDraw()
//    }
//
//    @OptIn(DelicateCoroutinesApi::class)
//    protected fun postDraw() {
//        GlobalScope.launch(Dispatchers.Default) {
//            synchronized(lock) {
//                if (bufferBitmap == null || bufferCanvas == null) {
//                    return@launch
//                }
//                if (bufferBitmap2 == null || bufferCanvas2 == null) {
//                    return@launch
//                }
//                isUse1 = !isUse1
//                if (isUse1) {
//                    log("开始绘制buffer1")
//                    synchronized(lockBuffer1) {
//                        bufferCanvas?.drawColor(Color.BLACK)
//                        bufferCanvas?.let { drawBuffering(canvas = it) }
//                    }
//                    log("结束绘制buffer1")
//                } else {
//                    log("开始绘制buffer2")
//                    synchronized(lockBuffer2) {
//                        bufferCanvas2?.drawColor(Color.BLACK)
//                        bufferCanvas2?.let { drawBuffering(canvas = it) }
//                        buffer2HasDraw = true
//                    }
//                    log("结束绘制buffer2")
//                }
//                invalidate()
//            }
//
//        }
//    }
//
//    protected abstract fun drawBuffering(canvas: Canvas)
//
//    private fun log(msg: String) {
//        if (isDebug) {
//            Timber.tag("BufferingView").e(msg)
//        }
//    }
//
//    final override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        // Timber.tag("BufferingView").e("drawBuffer2 = $isUse1")
//        if (!buffer2HasDraw) {
//            if (bufferBitmap == null || bufferCanvas == null) {
//                log("bufferBitmap or bufferCanvas is null")
//            }
//            synchronized(lockBuffer1) {
//                log("onDraw bufferBitmap")
//                bufferBitmap?.let {
//                    canvas.drawBitmap(it, 0f, 0f, butterPaint)
//                }
//            }
//        } else if (!isUse1) {
//            if (bufferBitmap == null || bufferCanvas == null) {
//                log("bufferBitmap or bufferCanvas is null")
//            }
//            log("onDraw bufferBitmap")
//            synchronized(lockBuffer1) {
//                bufferBitmap?.let {
//                    canvas.drawBitmap(it, 0f, 0f, butterPaint)
//                }
//            }
//        } else {
//            if (bufferBitmap2 == null || bufferCanvas2 == null) {
//                log("bufferBitmap2 or bufferCanvas2 is null")
//            }
//            log("onDraw bufferBitmap2")
//            synchronized(lockBuffer2) {
//                bufferBitmap2?.let {
//                    canvas.drawBitmap(it, 0f, 0f, butterPaint)
//                }
//            }
//        }
//    }
//}