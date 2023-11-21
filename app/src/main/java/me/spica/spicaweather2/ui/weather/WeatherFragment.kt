package me.spica.spicaweather2.ui.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fondesa.recyclerviewdivider.dividerBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import me.spica.spicaweather2.base.BindingFragment
import me.spica.spicaweather2.databinding.FragmentWeatherBinding
import me.spica.spicaweather2.persistence.entity.city.CityBean
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.view.weather_detail_card.HomeCardType

@AndroidEntryPoint
class WeatherFragment : BindingFragment<FragmentWeatherBinding>() {


    private val viewModel by viewModels<WeatherViewModel>()

    private lateinit var mainCardAdapter: MainCardAdapter

    private var currentCity: CityBean? = null

    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
        FragmentWeatherBinding = FragmentWeatherBinding.inflate(inflater, container, false)


    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun init() {
        currentCity = arguments?.getParcelable("city")
        currentCity?.let {
            viewModel.changeCity(it)
        }

        mainCardAdapter = MainCardAdapter(
            requireActivity(),
            viewBinding.rvCards,
            lifecycleScope
        )

        requireContext()
            .dividerBuilder()
            .colorRes(android.R.color.transparent)
            .size(12.dp.toInt())
            .showFirstDivider()
            .showLastDivider()
            .build().addTo(viewBinding.rvCards)

        viewBinding.rvCards.adapter = mainCardAdapter

        mainCardAdapter.setItems(
            listOf(
                HomeCardType.NOW_WEATHER,
                HomeCardType.TODAY_EXTRA,
                HomeCardType.TIPS,
//                HomeCardType.HOUR_WEATHER,
//                HomeCardType.SUNRISE,
                HomeCardType.AIR
            )
        )


        lifecycleScope.launch {
            viewModel.weatherFlow
                .filterNotNull()
                .collectLatest {
                    mainCardAdapter.notifyData(it)
                }
        }

    }
}