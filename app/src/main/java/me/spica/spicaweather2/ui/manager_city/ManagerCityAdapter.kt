package me.spica.spicaweather2.ui.manager_city

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.view.view_group.ItemCityManagerLayout

class ManagerCityAdapter : RecyclerView.Adapter<ManagerCityAdapter.ViewHolder>() {

    private val items: MutableList<CityWithWeather> = arrayListOf<CityWithWeather>()


    init {
        setHasStableIds(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<CityWithWeather>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout = itemView as ItemCityManagerLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemCityManagerLayout(parent.context))

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        return items[position].city.cityName.hashCode().toLong()
    }


    var deleteCityClickListener: ((CityWithWeather) -> Unit)? = null

    var itemClickListener: ((CityWithWeather,View) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layout.tag = items[position].city.cityName
        holder.layout.setData(items[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(items[position],holder.layout)
        }
        holder.layout.iconDelete.setOnClickListener {
            deleteCityClickListener?.invoke(items[position])
        }
    }
}