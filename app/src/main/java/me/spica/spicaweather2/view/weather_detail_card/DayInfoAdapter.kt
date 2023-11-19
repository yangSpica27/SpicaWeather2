package me.spica.spicaweather2.view.weather_detail_card

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.getAnimRes
import me.spica.spicaweather2.databinding.ItemDayWeather2Binding
import me.spica.spicaweather2.persistence.entity.weather.DailyWeatherBean
import me.spica.spicaweather2.tools.getColorWithAlpha
import java.text.SimpleDateFormat
import java.util.*

class DayInfoAdapter : RecyclerView.Adapter<DayInfoAdapter.ViewHolder>() {

    val items = mutableListOf<DailyWeatherBean>()

    var themeColor:Int = Color.BLACK


    // 格式化为"周几"
    private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

    // 用于排序的列表
    private val sortList = mutableListOf<DailyWeatherBean>()

    //点击监听
    var itemClickListener: (DailyWeatherBean) -> Unit = {}

    // 最近几日最低气温中最低的
    private var minTempTop = 0

    // 最近几日最低气温中最高的
    private var maxTempTop = 0

    // 最近几日最高气温中最低的
    private var minTempBottom = 0

    // 最近几日最高气温中最高的
    private var maxTempBottom = 0

    fun syncTempMaxAndMin() {
        sortList.clear()
        sortList.addAll(items)
        sortList.sortBy {
            it.maxTemp
        }
        maxTempTop = sortList.last().maxTemp
        minTempTop = sortList.first().maxTemp
        sortList.sortBy {
            it.minTemp
        }
        maxTempBottom = sortList.last().minTemp
        minTempBottom = sortList.first().minTemp
    }


    class ViewHolder(val ItemDayWeather2Binding: ItemDayWeather2Binding) : RecyclerView.ViewHolder(ItemDayWeather2Binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDayWeather2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ItemDayWeather2Binding.tvDay.text = sdfWeek.format(items[position].fxTime())
        if (position == 0) {
            holder.ItemDayWeather2Binding.tvDay.text = "今天"
            holder.ItemDayWeather2Binding.tvDay.setTextColor(themeColor)
        } else {
            holder.ItemDayWeather2Binding.tvDay.setTextColor(holder.itemView.context.getColor(R.color.textColorPrimary))
        }
        holder.ItemDayWeather2Binding.maxTemp.text = "${items[position].maxTemp}℃"
        holder.ItemDayWeather2Binding.minTemp.text = "${items[position].minTemp}℃"
        holder.ItemDayWeather2Binding.icon.setAnimation(
            items[position].getWeatherType().getAnimRes()
        )
        holder.ItemDayWeather2Binding.tvWeatherText.text = "白天 ${items[position].weatherNameDay} / 夜间 ${items[position].weatherNameNight}"
        holder.ItemDayWeather2Binding.motionContent.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) = Unit
            override fun onTransitionChange(motionLayout: MotionLayout, startId: Int, endId: Int, progress: Float) = Unit
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                if (currentId == R.id.end) {
                    holder.ItemDayWeather2Binding.btnExpand.animate().rotation(-90f)
                } else {
                    holder.ItemDayWeather2Binding.btnExpand.animate().rotation(90f)
                }
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) = Unit
        })
        holder.ItemDayWeather2Binding.tempView.apply {
            maxValue = maxTempTop
            minValue = minTempBottom
            currentMaxValue = items[position].maxTemp
            currentMinValue = items[position].minTemp
        }
        holder.ItemDayWeather2Binding.tempView.init()
        holder.ItemDayWeather2Binding.icon.progress = .5f
        holder.ItemDayWeather2Binding.icon.setMaxProgress(.5f)
        val dayExtraInfoAdapter = DayExtraInfoAdapter()
        dayExtraInfoAdapter.backgroundColor = getColorWithAlpha(.2f,themeColor)
        dayExtraInfoAdapter.items.clear()
        dayExtraInfoAdapter.items.addAll(
            listOf(
                DayExtraInfoAdapter.ShowData(
                    "湿度",
                    "${items[position].water}%",
                    R.drawable.ic_water
                ),
                DayExtraInfoAdapter.ShowData(
                    "降水量",
                    "${items[position].precip}mm",
                    R.drawable.ic_heavy_rain_outline
                ),
                DayExtraInfoAdapter.ShowData(
                    "风速",
                    "${items[position].windSpeed}km/h",
                    R.drawable.ic_wind
                ),
                DayExtraInfoAdapter.ShowData(
                    "紫外线强度",
                    items[position].uv,
                    R.drawable.ic_ux
                ),
                DayExtraInfoAdapter.ShowData(
                    "云层覆盖率",
                    "${items[position].cloud}%",
                    R.drawable.ic_cloud_outline
                ),
                DayExtraInfoAdapter.ShowData(
                    "能见度",
                    "${items[position].cloud}km",
                    R.drawable.ic_vis
                ),
            )
        )
        holder.ItemDayWeather2Binding.rvWeatherInfo.adapter = dayExtraInfoAdapter
    }
}