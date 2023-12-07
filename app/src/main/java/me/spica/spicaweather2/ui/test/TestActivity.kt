package me.spica.spicaweather2.ui.test

import android.graphics.Color
import android.os.Bundle
import android.os.HandlerThread
import android.view.ViewGroup
import me.spica.spicaweather2.view.SunView
import rikka.material.app.MaterialActivity

class TestActivity : MaterialActivity() {

    private val openGLSurfaceView: SunView by lazy {
        SunView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }
    }

    private val handlerThread = HandlerThread("test").apply {
        start()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(openGLSurfaceView)
    }


}
