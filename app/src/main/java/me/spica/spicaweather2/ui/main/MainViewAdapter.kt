package me.spica.spicaweather2.ui.main

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.view_group.NoWeatherDataLayout
import me.spica.spicaweather2.view.view_group.WeatherMainLayout2

/**
 * 主页的adapter
 */
class MainViewAdapter : RecyclerView.Adapter<MainViewAdapter.ViewHolder>() {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_DATA = 1
    }

    private val items = arrayListOf<CityWithWeather>()

    init {
//        setHasStableIds(true)
    }



    @SuppressLint("NotifyDataSetChanged")
    fun updateCities(cities: List<CityWithWeather>) {
        this.items.clear()
        this.items.addAll(cities)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setWeather(weather: Weather) {
            (itemView as WeatherMainLayout2).initData(weather)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_EMPTY) {
            return ViewHolder(NoWeatherDataLayout(parent.context))
        }
        return ViewHolder(WeatherMainLayout2(parent.context))
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        if (items[position].weather != null) {
            return TYPE_DATA
        }
        return TYPE_EMPTY
    }

    override fun getItemId(position: Int): Long {
        if (items[position].weather != null) {
            return items[position].city.cityName.hashCode().toLong()
        }
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = items[position].weather
        if (weather != null) {
            holder.setWeather(weather)
        }
    }
}
