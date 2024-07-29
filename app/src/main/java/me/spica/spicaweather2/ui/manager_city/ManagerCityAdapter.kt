package me.spica.spicaweather2.ui.manager_city

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.CityWithWeather
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.tools.getScreenWidth
import me.spica.spicaweather2.view.view_group.ItemCityManagerLayout

/**
 * 城市管理界面的Adapter
 */
class ManagerCityAdapter : RecyclerView.Adapter<ManagerCityAdapter.ViewHolder>() {
    val items: MutableList<CityWithWeather> = arrayListOf()

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_ADD = 1
    }

    // 是否是选择模式
    var isSelectMode = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
            if (!value) {
                selectedCitiesSet.clear()
            }
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

    fun getSelectCityNames() = selectedCitiesSet.toList()

    class ViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        fun itemLayout() = itemView as ItemCityManagerLayout
    }

    private val selectedCitiesSet = mutableSetOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        if (ITEM_TYPE_NORMAL == viewType) {
            ViewHolder(ItemCityManagerLayout(parent.context))
        } else if (ITEM_TYPE_ADD == viewType) {
            ViewHolder(
                MaterialButton(parent.context).apply {
                    layoutParams =
                        RecyclerView
                            .LayoutParams(
                                RecyclerView.LayoutParams.MATCH_PARENT,
                                RecyclerView.LayoutParams.WRAP_CONTENT,
                            ).apply {
                                updateMargins(
                                    left = 14.dp.toInt() + context.getScreenWidth() / 5,
                                    right = 14.dp.toInt() + context.getScreenWidth() / 5,
                                )
                            }
                    setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue_600))
                    cornerRadius = 24.dp.toInt()
                    text = "新增城市"
                },
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
        return items[position]
            .city.cityName
            .hashCode()
            .toLong()
    }

    var itemClickListener: ((Int, View) -> Unit)? = null

    var itemLongClickListener: ((Int, View) -> Unit)? = null

    var addCityClickListener: (() -> Unit)? = null

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        if (holder.itemViewType == ITEM_TYPE_NORMAL) {
            holder.itemLayout().tag = items[position].city.cityName
            holder.itemLayout().setData(items[position])
            holder.itemLayout().isSelect = selectedCitiesSet.contains(items[position].city.cityName)
            holder.itemView.setOnClickListener {
                if (!isSelectMode) {
                    itemClickListener?.invoke(position, holder.itemLayout())
                } else {
                    holder.itemLayout().isSelect = !holder.itemLayout().isSelect
                    if (holder.itemLayout().isSelect) {
                        selectedCitiesSet.add(items[position].city.cityName)
                    } else {
                        selectedCitiesSet.remove(items[position].city.cityName)
                    }
                }
            }
            holder.itemView.setOnLongClickListener {
                if (!isSelectMode) {
                    itemLongClickListener?.invoke(position, holder.itemLayout())
                    return@setOnLongClickListener true
                } else {
                    return@setOnLongClickListener false
                }
            }
            holder.itemLayout().post {
                holder.itemLayout().isSelectable = isSelectMode
            }
        } else if (holder.itemViewType == ITEM_TYPE_ADD) {
            holder.itemView.setOnClickListener {
                addCityClickListener?.invoke()
            }
        }
    }
}
