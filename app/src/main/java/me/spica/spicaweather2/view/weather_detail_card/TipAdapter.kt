package me.spica.spicaweather2.view.weather_detail_card

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.spica.spicaweather2.databinding.ItemTipsBinding
import me.spica.spicaweather2.persistence.entity.weather.LifeIndexBean
import me.spica.spicaweather2.tools.getColorWithAlpha
// 生活指数适配器
class TipAdapter : RecyclerView.Adapter<TipAdapter.ViewHolder>() {

    // 数据
    val items: MutableList<LifeIndexBean> = mutableListOf()

    var themeColor:Int = Color.WHITE

    class ViewHolder(val itemTipsBinding: ItemTipsBinding) :
        RecyclerView.ViewHolder(itemTipsBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        ViewHolder {
        val itemTipsBinding = ItemTipsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemTipsBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bgDrawable =  holder.itemTipsBinding.root.background
        bgDrawable.colorFilter = PorterDuffColorFilter(
            getColorWithAlpha(.08f,themeColor), PorterDuff.Mode.SRC_IN)
        holder.itemTipsBinding.root.background = bgDrawable

        items[position].let { item ->
            holder.itemTipsBinding.tvName.text = item.name
            holder.itemTipsBinding.tvDesc.text = item.category
            holder.itemTipsBinding.ivIcon.load(item.iconRes())
        }
    }

    override fun getItemCount(): Int = items.size
}
