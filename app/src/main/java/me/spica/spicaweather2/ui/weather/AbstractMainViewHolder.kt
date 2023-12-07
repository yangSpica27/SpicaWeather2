package me.spica.spicaweather2.ui.weather

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard

open class AbstractMainViewHolder(private val card: SpicaWeatherCard, itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    fun bindView(weather: Weather) {
        card.bindData(weather)
    }

    fun reset() {
        card.resetAnim()
    }

    private val rect = Rect()
    fun checkEnterScreen() {
        try {
            val isVisible = itemView.getGlobalVisibleRect(rect)
            card.checkEnterScreen(isVisible && rect.bottom - rect.top >= itemView.height / 10f)
        } catch (e: Exception) {
            e.message
        }
    }
}
