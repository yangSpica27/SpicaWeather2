package me.spica.spicaweather2.ui.weather

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

open class AbstractMainViewHolder(private val card: SpicaWeatherCard, itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    // 绑定数据
    fun bindView(weather: Weather) {
        card.bindData(weather)
    }

    // 重置入屏动画
    fun reset() {
        card.resetAnim()
    }


    // 复用的rect 防止内存抖动
    private val itemVisibleRect = Rect()

    // 检查是否进入屏幕
    fun checkEnterScreen() {
        try {
            // 如果已经进入屏幕则不再检查
            if (card.hasInScreen) return
            // 获取itemView在屏幕中的位置
            val isVisible = itemView.getGlobalVisibleRect(itemVisibleRect)
            // 如果itemView在屏幕中可见 执行入屏动画
            card.checkEnterScreen(isVisible && itemVisibleRect.bottom - itemVisibleRect.top >= itemView.height / 10f)
        } catch (e: Exception) {
            e.message
        }
    }
}
