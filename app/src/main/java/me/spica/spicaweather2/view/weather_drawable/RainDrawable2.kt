package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.weather_bg.RainFlake
import me.spica.spicaweather2.weather_anim_counter.RainParticleManager

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
        synchronized(this) {
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
                rainEffectCounter.system.particleCount <= 2000
            ) {
                lastAddRainTime = System.currentTimeMillis()
                rainEffectCounter.createRainItem()
            }
        }
    }

    // 用于绘制碰撞的水滴
    private val listPoints = arrayListOf<Float>()

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(lock) {
            // 绘制无碰撞的前景
            rainFlakes.forEach {
                it.onlyDraw(canvas)
            }
            // 绘制碰撞水滴
            listPoints.clear()
            for (i in 0..(rainEffectCounter.system.particleCount ?: 0)) {
                val x = (rainEffectCounter.system.getParticlePositionX(i) ?: 0f) * 60
                val y = (rainEffectCounter.system.getParticlePositionY(i) ?: 0f) * 60
                listPoints.add(x)
                listPoints.add(y)
                if (x < 0 || x >= width) {
                    rainEffectCounter.system.destroyParticle(i)
                }
                if (y >= height) {
                    rainEffectCounter.system.destroyParticle(i)
                }
            }
            canvas.drawPoints(listPoints.toFloatArray(), rainPaint)
        }
    }

    fun release() {
        rainEffectCounter.destroy()
    }

}