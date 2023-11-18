package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.weather_bg.RainFlake


class RainDrawable : WeatherDrawable() {

    // 绘制雨水的paint
    private val rainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4.dp
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    // 雨水的合集
    private var rains: ArrayList<RainFlake> = arrayListOf()

    fun ready(width: Int, height: Int) {
        synchronized(rains){
            rains.clear()
            for (i in 0 until 30) {
                rains.add(RainFlake.create(width, height, rainPaint))
            }
        }

    }

    fun calculate(width: Int, height: Int) {
        synchronized(rains){
            rains.forEach { rain ->
                rain.calculation(width, height)
            }
        }
    }


    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(rains){
            rains.forEach { rainFlake ->
                rainFlake.onlyDraw(canvas)
            }
        }
    }
}