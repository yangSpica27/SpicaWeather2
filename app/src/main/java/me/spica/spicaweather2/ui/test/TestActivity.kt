package me.spica.spicaweather2.ui.test

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.ViewGroup
import rikka.material.app.MaterialActivity

class TestActivity : MaterialActivity() {

    private val openGLSurfaceView: GLSurfaceView by lazy {
        GLSurfaceView(this).apply {
            setEGLContextClientVersion(3)
//            setRenderer(LiquidRender())
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private val handlerThread = HandlerThread("test").apply {
        start()
    }

    private val handler = Handler(handlerThread.looper)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(openGLSurfaceView)
        handler.post(runnable)
    }

    private val runnable = object : Runnable {
        override fun run() {
            openGLSurfaceView.requestRender()
            handler.post(this)
        }
    }

    override fun onResume() {
        super.onResume()
        openGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        openGLSurfaceView.onPause()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }
}
