package me.spica.spicaweather2.ui.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.common.WeatherType
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.ui.air_introduce.AirIntroductionActivity
import me.spica.spicaweather2.view.view_group.AirCardLayout
import me.spica.spicaweather2.view.view_group.DailyWeatherLayout
import me.spica.spicaweather2.view.view_group.HourlyCardLayout
import me.spica.spicaweather2.view.view_group.TodayDescLayout
import me.spica.spicaweather2.view.view_group.SunriseCardLayout
import me.spica.spicaweather2.view.view_group.TipsLayout
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType

class MainCardAdapter(
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<AbstractMainViewHolder>() {

    // 卡片类型
    private val items = arrayListOf<HomeCardType>()

    // 天气数据
    private var weather: Weather? = null

    init {
        setHasStableIds(true)
        recyclerView.setItemViewCacheSize(WeatherType.values().size)
    }

    // 设置卡片类型
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<HomeCardType>) {
        this.items.clear()
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractMainViewHolder {
        when (viewType) {
            HomeCardType.DAY_WEATHER.code -> {
                val itemView = DailyWeatherLayout(parent.context)
                return AbstractMainViewHolder(itemView, itemView).apply {
                    reset()
                }
            }

            HomeCardType.HOUR_WEATHER.code -> {
                val itemView = HourlyCardLayout(context = parent.context)
                return AbstractMainViewHolder(itemView, itemView).apply {
                    reset()
                }
            }

            HomeCardType.TIPS.code -> {
                val itemView = TipsLayout(parent.context)
                // 生活指数卡片点击跳转
                return AbstractMainViewHolder(itemView, itemView).apply {
                    reset()
                }
            }

            HomeCardType.NOW_WEATHER.code -> {
                val itemView = TodayDescLayout(parent.context)
                return AbstractMainViewHolder(itemView, itemView).apply {
                    reset()
                }
            }

            HomeCardType.SUNRISE.code -> {
                val itemView = SunriseCardLayout(context = parent.context)
                return AbstractMainViewHolder(itemView, itemView).apply {
                    reset()
                }
            }

            HomeCardType.AIR.code -> {
                val itemView = AirCardLayout(parent.context)
                itemView.setOnClickListener {
                    // 空气质量卡片点击跳转
                    parent.context.startActivity(Intent(parent.context, AirIntroductionActivity::class.java))
                }
                return AbstractMainViewHolder(itemView, itemView).apply {
                    reset()
                }
            }

            else -> {
                val itemView = DailyWeatherLayout(parent.context)
                return AbstractMainViewHolder(itemView, itemView).apply {
                    reset()
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return items[position].code
    }

    override fun onBindViewHolder(holder: AbstractMainViewHolder, position: Int) {
        weather?.let {
            // 绑定数据
            holder.bindView(it)
            // 内容宽高发生变化 重新测量
            holder.itemView.doOnPreDraw {
                holder.itemView.requestLayout()
            }
        }
    }

    // 更新数据
    @SuppressLint("NotifyDataSetChanged")
    fun notifyData(weather: Weather) {
        this.weather = weather
        notifyDataSetChanged()
        recyclerView.doOnPreDraw {
            onScroll()
        }
    }

    // 滚动时检查是否进入屏幕
    fun onScroll() {
        var holder: AbstractMainViewHolder?
        if (itemCount == 0) return
        for (i in 0 until itemCount) {
            holder = recyclerView.findViewHolderForAdapterPosition(i) as AbstractMainViewHolder?
            holder?.checkEnterScreen()
        }
    }

    override fun getItemId(position: Int): Long {
        return "${items[position].code},${weather?.todayWeather?.obsTime}".hashCode().toLong()
    }

    override fun getItemCount(): Int = items.size
}
