package me.spica.spicaweather2.ui.add_city

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.tools.dp

/**
 * 添加城市的adapter
 */
class AddCityAdapter : RecyclerView.Adapter<AddCityAdapter.ViewHolder>() {

    private val items: MutableList<CityBean> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    var selectCityListener: ((CityBean) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<CityBean>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(val item: AppCompatTextView) : RecyclerView.ViewHolder(item) {
        fun setTextView(text: String) {
            item.text = text.trim()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AppCompatTextView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                )
                setPadding(
                    14.dp.toInt(),
                    12.dp.toInt(),
                    14.dp.toInt(),
                    12.dp.toInt()
                )
                setTextAppearance(R.style.TextAppearance_Material3_HeadlineSmall)
            }
        )
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        return items[position].cityName.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.setTextView(item.cityName)
        holder.itemView.setOnClickListener {
            selectCityListener?.invoke(item)
        }
    }
}
