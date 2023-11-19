package me.spica.spicaweather2.ui.weather

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.weather_detail_card.AirCard
import me.spica.spicaweather2.view.weather_detail_card.DailyWeatherCard
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType
import me.spica.spicaweather2.view.weather_detail_card.HourWeatherCard
import me.spica.spicaweather2.view.weather_detail_card.NowWeatherCard
import me.spica.spicaweather2.view.weather_detail_card.SunriseCard
import me.spica.spicaweather2.view.weather_detail_card.TipsCard


class MainCardAdapter(
  private val activity: Activity,
  private val recyclerView: RecyclerView,
  private val scope: CoroutineScope
) : RecyclerView.Adapter<AbstractMainViewHolder>() {


  private val items = arrayListOf<HomeCardType>()

  private var weather: Weather? = null

  init {
    recyclerView.setItemViewCacheSize(10)
  }

  @SuppressLint("NotifyDataSetChanged")
  fun setItems(items: List<HomeCardType>) {
    this.items.clear()
    this.items.addAll(items)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
      AbstractMainViewHolder {
    val lp = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    when (viewType) {
      HomeCardType.DAY_WEATHER.code -> {
        val itemView = DailyWeatherCard(parent.context)
        itemView.layoutParams = lp
        return AbstractMainViewHolder(itemView, itemView)
      }
      HomeCardType.HOUR_WEATHER.code -> {
        val itemView = HourWeatherCard(parent.context)
        itemView.layoutParams = lp
        return AbstractMainViewHolder(itemView, itemView)
      }
      HomeCardType.TIPS.code -> {
        val itemView = TipsCard(parent.context)
        itemView.layoutParams = lp
        // 生活指数卡片点击跳转
        return AbstractMainViewHolder(itemView, itemView)
      }
      HomeCardType.NOW_WEATHER.code -> {
        val itemView = NowWeatherCard(parent.context)
        itemView.layoutParams = lp
        itemView.setOnClickListener {
//          todayWeatherDetailDialog.show(activity)
        }
        return AbstractMainViewHolder(itemView, itemView)
      }
      HomeCardType.SUNRISE.code -> {
        val itemView = SunriseCard(parent.context)
        itemView.layoutParams = lp
        return AbstractMainViewHolder(itemView, itemView)
      }
      HomeCardType.AIR.code -> {
        val itemView = AirCard(parent.context)
        itemView.layoutParams = lp
        itemView.setOnClickListener {
          if (weather?.air?.fxLink?.isEmpty() == false) {
          }
        }
        return AbstractMainViewHolder(itemView, itemView)
      }
      else -> {
        val itemView = DailyWeatherCard(parent.context)
        itemView.layoutParams = lp
        return AbstractMainViewHolder(itemView, itemView)
      }
    }
  }


  override fun getItemViewType(position: Int): Int {
    return items[position].code
  }

  override fun onBindViewHolder(holder: AbstractMainViewHolder, position: Int) {
    weather?.let {
      holder.bindView(it)
    }
    holder.itemView.post {
      scope.launch (Dispatchers.Default){
        holder.checkEnterScreen()
      }
    }
  }


  @SuppressLint("NotifyDataSetChanged")
  fun notifyData(weather: Weather) {
    this.weather = weather
    notifyDataSetChanged()
    recyclerView.scrollToPosition(0)
  }


  fun onScroll() {
    var holder: AbstractMainViewHolder
    if (itemCount == 0) return
    for (i in 0 until itemCount) {
      if (recyclerView.findViewHolderForAdapterPosition(i) != null) {
        holder = recyclerView.findViewHolderForAdapterPosition(i) as AbstractMainViewHolder
        holder.checkEnterScreen()
      }
    }
  }

  override fun getItemId(position: Int): Long {
   return items[position].code.toLong()
  }

  override fun getItemCount(): Int = items.size


}