package me.spica.spicaweather2.view.weather_drawable

import android.graphics.Canvas


abstract class WeatherDrawable {


    // 入场动画
    open fun startAnim() = Unit

    // 出场动画/取消动画
    open fun cancelAnim() = Unit

    // 初始化
    abstract fun ready(width: Int, height: Int)

    // 绘制
    abstract fun doOnDraw(canvas: Canvas, width: Int, height: Int)

    open fun calculate(width: Int, height: Int) = Unit

    open fun setBackgroundY(y: Int) = Unit
}
