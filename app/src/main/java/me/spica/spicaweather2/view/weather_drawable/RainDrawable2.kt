package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.weather_bg.RainFlake
import me.spica.spicaweather2.weather_anim_counter.RainParticleManager
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * 雨水的绘制
 */
class RainDrawable2 : WeatherDrawable() {


    // 绘制雨水的paint
    private val rainPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 2.dp
        color = Color.parseColor("#80E8E8E8")
        style = Paint.Style.FILL
    }

    private val rainPaint2 = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4.dp
        color = Color.parseColor("#80E8E8E8")
        style = Paint.Style.FILL
    }

    private val lock = Any()

    private val rainEffectCounter = RainParticleManager()

    private val rainFlakes = arrayListOf<RainFlake>()

    // 初始化
    fun ready(width: Int, height: Int) {
        synchronized(lock) {
            rainEffectCounter.init(width, height)
            for (i in 0..200) {
                rainFlakes.add(
                    RainFlake.create(
                        width,
                        height,
                        rainPaint2,
                        Color.parseColor("#65E8E8E8")
                    )
                )
            }
        }
    }

    // 设置背景刚体的Y轴
    fun setBackgroundY(y: Int) {
        rainEffectCounter.setBackgroundY(y)
    }


    // 上一次添加雨滴的时间
    private var lastAddRainTime = 0L


    // 计算雨滴的位置
    fun calculate(width: Int, height: Int) {
        synchronized(lock) {
            rainFlakes.forEach {
                it.calculation(width, height)
            }
            rainEffectCounter.run()
            if ((System.currentTimeMillis() - lastAddRainTime > 50) &&
                rainEffectCounter.system.particleCount <= RainParticleManager.ParticleMaxCount
            ) {
                lastAddRainTime = System.currentTimeMillis()
                rainEffectCounter.createRainItem()
            }
        }

    }


    // 粒子的位置缓冲区
    private val mParticlePositionBuffer: ByteBuffer =
        ByteBuffer
            .allocateDirect(2 * 4 * RainParticleManager.ParticleMaxCount)
            .order(ByteOrder.nativeOrder())

    // 粒子的位置数组
    private val positionArray = FloatArray(RainParticleManager.ParticleMaxCount * 2)

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(lock) {
            // 绘制无碰撞的前景
            rainFlakes.forEach {
                it.onlyDraw(canvas)
            }
            mParticlePositionBuffer.rewind()
            rainEffectCounter.system.copyPositionBuffer(
                0,
                rainEffectCounter.system.particleCount,
                mParticlePositionBuffer
            )
            mParticlePositionBuffer.asFloatBuffer().get(positionArray)
            for (i in positionArray.indices) {
                positionArray[i] = positionArray[i] * 60
            }
            canvas.drawPoints(positionArray, rainPaint)
        }
    }

    fun release() {
        rainEffectCounter.destroy()
        mParticlePositionBuffer.clear()
    }

}