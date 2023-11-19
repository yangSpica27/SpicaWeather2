package me.spica.spicaweather2.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.ui.weather.WeatherFragment

class MainPagerAdapter(
    fragmentActivity: FragmentActivity
) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentPool = hashMapOf<String, Fragment>()


    val diffUtil = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<CityBean>() {
            override fun areItemsTheSame(oldItem: CityBean, newItem: CityBean): Boolean =
                oldItem.cityName == newItem.cityName

            override fun areContentsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = false

        }
    )

    override fun getItemCount(): Int = diffUtil.currentList.size

    override fun createFragment(position: Int): Fragment {


        if (fragmentPool.containsKey(diffUtil.currentList[position].cityName)) {
            return fragmentPool[diffUtil.currentList[position].cityName]!!
        }

        val fragment = WeatherFragment()
        fragment.arguments = Bundle().apply {
            putParcelable("city", diffUtil.currentList[position])
        }
        fragmentPool[diffUtil.currentList[position].cityName] = fragment
        return fragment
    }


}