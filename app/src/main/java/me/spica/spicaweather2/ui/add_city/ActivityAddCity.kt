package me.spica.spicaweather2.ui.add_city

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.view_group.ActivityAddCityLayout
import me.spica.spicaweather2.work.DataSyncWorker
import rikka.material.app.MaterialActivity

/**
 * 添加城市
 */
@AndroidEntryPoint
class ActivityAddCity : MaterialActivity() {

    private val layout by lazy {
        ActivityAddCityLayout(this)
    }

    private val addCityAdapter = AddCityAdapter()

    private val cityViewModel by viewModels<CityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        init()
    }

    private fun init() {
        // handleBack()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        layout.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        dividerBuilder()
            .color(getColor(R.color.line_divider))
            .size(2, TypedValue.COMPLEX_UNIT_PX)
            .build()
            .addTo(layout.recyclerView)
        layout.recyclerView.adapter = addCityAdapter
        layout.searchBarLayout.editText.addTextChangedListener {
            cityViewModel.updateSearchKeyword(it.toString())
        }
        lifecycleScope.launch {
            cityViewModel.searchFlow.collectLatest {
                addCityAdapter.setItems(it)
            }
        }
        addCityAdapter.selectCityListener = {
            lifecycleScope.launch(Dispatchers.IO) {
                if (cityViewModel.getCount() >= 5) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ActivityAddCity, "最多添加5个城市", Toast.LENGTH_SHORT)
                            .show()
                    }
                    return@launch
                }
                cityViewModel.addCity(it)
                withContext(Dispatchers.Main) {
                    startService(Intent(this@ActivityAddCity, DataSyncWorker::class.java))
                    finish()
                }
            }
        }
    }

}
