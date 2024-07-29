package me.spica.spicaweather2.view.weather_bg

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import java.util.concurrent.locks.ReentrantLock
import kotlin.system.measureTimeMillis

abstract class SimpleDrawTask(
    // 绘制间隔
    val interval: Long = 16L,
    // 绘制逻辑
    val drawFunc: (Canvas) -> Unit,
) : IDrawTask {
    // 绘制线程
    private var handlerThread: HandlerThread? = null

    private var handler: Handler? = null

    private val lock = ReentrantLock()

    private var isRunning = false

    companion object {
        private val PAINT =
            Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                color = Color.TRANSPARENT
            }
        private val RECT = Rect()
    }

    override fun ready() {
        if (isRunning) {
            return
        }
        lock.lock()
        handlerThread = HandlerThread("SimpleDrawTask-${this.hashCode()}")
        handlerThread?.start()
        handler = Handler(handlerThread?.looper!!)
        handler?.post(drawRunnable)
        isRunning = true
        lock.unlock()
    }

    private val drawRunnable =
        object : Runnable {
            override fun run() {
                // 未初始化或者在结束流程中
                while (true) {
                    if (!isRunning || Thread.interrupted()) {
                        return
                    }
                    val count =
                        measureTimeMillis {
                            lock.lock()
                            val canvas = lockCanvas()
                            if (canvas == null) {
                                lock.unlock()
                                handler?.postDelayed(this, interval)
                                return
                            }
                            draw(canvas)
                            unlockCanvas(canvas)
                            lock.unlock()
                        }
                    val delay = interval - count
                    SystemClock.sleep(if (delay > 0) delay else 0)
                }
            }
        }

    override fun draw(canvas: Canvas) {
        if (!isRunning) {
            return
        }
        drawFunc(canvas)
    }

    private fun clearCanvas(canvas: Canvas) {
        canvas.drawColor(
            Color.TRANSPARENT,
            PorterDuff.Mode.CLEAR,
        )
        RECT.set(0, 0, canvas.width, canvas.height)
        canvas.drawRect(RECT, PAINT)
    }

    override fun destroy() {
        if (!isRunning) {
            return
        }
        lock.lock()
        isRunning = false
        handler?.removeCallbacksAndMessages(null)
        handlerThread?.quitSafely()
        try {
            // 等待正在执行的绘制结束 最多等待interval时间
            handlerThread?.join(interval)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        handlerThread?.interrupt()
        lock.unlock()
    }
}
