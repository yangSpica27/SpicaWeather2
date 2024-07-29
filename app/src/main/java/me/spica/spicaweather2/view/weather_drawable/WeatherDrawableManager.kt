package me.spica.spicaweather2.view.weather_drawable

import android.content.Context
import android.graphics.Canvas
import me.spica.spicaweather2.tools.getScreenHeight
import me.spica.spicaweather2.tools.getScreenWidth
import me.spica.spicaweather2.view.weather_bg.NowWeatherView

//  天气动画管理者
class WeatherDrawableManager(
    context: Context,
) {
    // 当前的动画类型
    private var currentAnimType = NowWeatherView.WeatherAnimType.SUNNY

    // 动画集合
    private val drawableMaps = HashMap<NowWeatherView.WeatherAnimType, WeatherDrawable>()

    init {
        register(NowWeatherView.WeatherAnimType.SUNNY, SunnyDrawable(context))
        register(NowWeatherView.WeatherAnimType.CLOUDY, CloudDrawable(context))
        register(NowWeatherView.WeatherAnimType.RAIN, RainDrawable2())
        register(NowWeatherView.WeatherAnimType.SNOW, SnowDrawable())
        register(NowWeatherView.WeatherAnimType.FOG, HazeDrawable(context))
        register(NowWeatherView.WeatherAnimType.HAZE, HazeDrawable(context))
        register(NowWeatherView.WeatherAnimType.UNKNOWN, CloudDrawable(context))
        register(NowWeatherView.WeatherAnimType.SANDSTORM, SandStormDrawable())
        ready(context.getScreenWidth(), context.getScreenHeight())
        getCurrentDrawable().startAnim()
    }

    fun ready(
        width: Int,
        height: Int,
    ) {
        drawableMaps.values.iterator().forEach {
            it.ready(width, height)
            it.setBackgroundY(cacheY)
        }
    }

    fun calculate(
        width: Int,
        height: Int,
    ) {
        synchronized(this) {
            drawableMaps[currentAnimType]?.calculate(width, height)
        }
    }

    private var cacheY = 0

    fun setBackgroundY(y: Int) {
        cacheY = y
        drawableMaps[currentAnimType]?.setBackgroundY(y)
    }

    fun setScrollY(y: Int) {
        drawableMaps[currentAnimType]?.setScrollY(y)
    }

    // 设置当前的天气动画
    fun setWeatherAnimType(weatherAnimType: NowWeatherView.WeatherAnimType) {
        if (currentAnimType != weatherAnimType) {
            currentAnimType = weatherAnimType
            release()
            getCurrentDrawable().startAnim()
            setBackgroundY(cacheY)
        }
    }

    // 绘制
    fun doOnDraw(
        canvas: Canvas,
        width: Int,
        height: Int,
    ) {
        synchronized(this) {
            getCurrentDrawable().doOnDraw(canvas, width, height)
        }
    }

    // 销毁
    fun release() {
        drawableMaps.values.iterator().forEach {
            it.cancelAnim()
        }
    }

    private fun register(
        weatherAnimType: NowWeatherView.WeatherAnimType,
        drawable: WeatherDrawable,
    ) {
        drawableMaps[weatherAnimType] = drawable
    }

    private fun getCurrentDrawable(): WeatherDrawable =
        drawableMaps[currentAnimType]
            ?: throw IllegalArgumentException("${currentAnimType}特效未注册")
}
