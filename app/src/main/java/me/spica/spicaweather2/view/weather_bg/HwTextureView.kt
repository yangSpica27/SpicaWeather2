package me.spica.spicaweather2.view.weather_bg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView

open class HwTextureView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    TextureView(ctx, attrs, defStyle) {

    private var userListener: SurfaceTextureListener? = null
    private var proxySurface: Surface? = null

    private val innerImplListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            proxySurface = Surface(surface)
            userListener?.onSurfaceTextureAvailable(surface, width, height)
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            userListener?.onSurfaceTextureSizeChanged(surface, width, height)
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            proxySurface?.release()
            return userListener?.onSurfaceTextureDestroyed(surface) ?: true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            userListener?.onSurfaceTextureUpdated(surface)
        }
    }

    init {
        super.setSurfaceTextureListener(innerImplListener)
    }

    override fun lockCanvas(): Canvas? {
        return proxySurface?.lockHardwareCanvas()
    }

    /**
     * Disabled in Hardware Accelerated mode.
     */
    override fun lockCanvas(dirty: Rect?): Canvas? {
        return null
    }

    override fun unlockCanvasAndPost(canvas: Canvas) {
        proxySurface?.unlockCanvasAndPost(canvas)
    }

    override fun getSurfaceTextureListener(): SurfaceTextureListener {
        return innerImplListener
    }

    override fun setSurfaceTextureListener(listener: SurfaceTextureListener?) {
        userListener = listener
    }
}