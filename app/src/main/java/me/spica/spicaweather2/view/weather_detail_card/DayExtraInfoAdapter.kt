package me.spica.spicaweather2.view.weather_detail_card

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.spica.spicaweather2.databinding.ItemTipsBinding

class DayExtraInfoAdapter : RecyclerView.Adapter<DayExtraInfoAdapter.ViewHolder>() {

    // 数据
    val items: MutableList<ShowData> = mutableListOf()

    var backgroundColor = Color.WHITE

    class ViewHolder(val itemTipsBinding: ItemTipsBinding) :
        RecyclerView.ViewHolder(itemTipsBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        ViewHolder {
        val itemTipsBinding = ItemTipsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemTipsBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bgDrawable = holder.itemTipsBinding.root.background
        bgDrawable.colorFilter = PorterDuffColorFilter(
            backgroundColor, PorterDuff.Mode.SRC_IN
        )
         holder.itemTipsBinding.root.background = bgDrawable
        items[position].let { item ->
            holder.itemTipsBinding.tvName.text = item.title
            holder.itemTipsBinding.tvDesc.text = item.value
            holder.itemTipsBinding.ivIcon.load(item.iconRes)
        }
    }

    override fun getItemCount(): Int = items.size

    data class ShowData(
        val title: String,
        val value: String,
        @DrawableRes val iconRes: Int
    )
}
