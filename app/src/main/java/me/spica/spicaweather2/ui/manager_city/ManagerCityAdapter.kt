package me.spica.spicaweather2.ui.manager_city

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.view_group.ItemCityManagerLayout

/**
 * 城市管理界面的Adapter
 */
class ManagerCityAdapter : RecyclerView.Adapter<ManagerCityAdapter.ViewHolder>() {

    private val items: MutableList<CityWithWeather> = arrayListOf()

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_ADD = 1
    }

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
        fun itemLayout() = itemView as ItemCityManagerLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        if (ITEM_TYPE_NORMAL == viewType) {
            ViewHolder(ItemCityManagerLayout(parent.context))
        } else if (ITEM_TYPE_ADD == viewType) {
            ViewHolder(
                MaterialButton(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        updateMargins(left = 14.dp.toInt(), right = 14.dp.toInt())
                    }
                    cornerRadius = 16.dp.toInt()
                    text = "新增城市"
                }
            )
        } else {
            ViewHolder(ItemCityManagerLayout(parent.context))
        }

    override fun getItemCount(): Int = (items.size + 1).coerceAtMost(5)

    override fun getItemViewType(position: Int): Int {
        if (position == items.size) return ITEM_TYPE_ADD
        return ITEM_TYPE_NORMAL
    }

    override fun getItemId(position: Int): Long {
        if (position == items.size) return -1
        return items[position].city.cityName.hashCode().toLong()
    }

    var deleteCityClickListener: ((CityWithWeather) -> Unit)? = null

    var itemClickListener: ((Int, View) -> Unit)? = null

    var addCityClickListener: (() -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_TYPE_NORMAL) {
            holder.itemLayout().tag = items[position].city.cityName
            holder.itemLayout().setData(items[position])
            holder.itemView.setOnClickListener {
                itemClickListener?.invoke(position, holder.itemLayout())
            }
            holder.itemLayout().iconDelete.setOnClickListener {
                deleteCityClickListener?.invoke(items[position])
            }
        } else if (holder.itemViewType == ITEM_TYPE_ADD) {
            holder.itemView.setOnClickListener {
                addCityClickListener?.invoke()
            }
        }
    }
}
