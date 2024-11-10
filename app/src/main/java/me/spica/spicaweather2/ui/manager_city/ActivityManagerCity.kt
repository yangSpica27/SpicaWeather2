package me.spica.spicaweather2.ui.manager_city

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.MessageEvent
import me.spica.spicaweather2.tools.MessageType
import me.spica.spicaweather2.tools.doOnMainThreadIdle
import me.spica.spicaweather2.tools.dp
import me.spica.spicaweather2.tools.hide
import me.spica.spicaweather2.tools.show
import me.spica.spicaweather2.tools.toast
import me.spica.spicaweather2.ui.add_city.ActivityAddCity
import me.spica.spicaweather2.view.Home2ManagerView
import me.spica.spicaweather2.view.Manager2HomeView
import me.spica.spicaweather2.view.view_group.ActivityManagerCityLayout
import org.greenrobot.eventbus.EventBus
import rikka.material.app.MaterialActivity
import timber.log.Timber
import java.util.*

/**
 * 城市管理页面
 */
@AndroidEntryPoint
class ActivityManagerCity : MaterialActivity() {
    companion object {
        const val ARG_CITY_NAME = "arg_position"
    }

    private val layout by lazy {
        ActivityManagerCityLayout(this)
    }

    // 是否已经执行过入场动画
    private var hasDoEnterAnim = false

    private val home2ManagerView by lazy {
        Home2ManagerView(this)
    }

    private val adapter = ManagerCityAdapter()

    private val viewModel by viewModels<CityManagerViewModel>()

    private val itemTouchHelper =
        CityItemTouchHelper(
            isDrag = false,
            onMove = { viewHolder, target ->
                viewModel.moveCity(
                    adapter.items[viewHolder.absoluteAdapterPosition].city,
                    adapter.items[target.absoluteAdapterPosition].city,
                )
                Collections.swap(
                    adapter.items,
                    viewHolder.absoluteAdapterPosition,
                    target.absoluteAdapterPosition,
                )
                adapter.notifyItemMoved(
                    viewHolder.absoluteAdapterPosition,
                    target.absoluteAdapterPosition,
                )
            },
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        setContentView(layout)
        init()
    }

    @Suppress("DEPRECATION")
    private fun init() {
        setSupportActionBar(layout.titleBar)
        layout.recyclerView.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false,
            )

        WindowCompat.getInsetsController(this.window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        dividerBuilder()
            .showLastDivider()
            .showFirstDivider()
            .asSpace()
            .size(12.dp.toInt())
            .build()
            .addTo(layout.recyclerView)

        home2ManagerView.attachToRootView()

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        layoutManager.initialPrefetchItemCount = 10
        layout.recyclerView.layoutManager = layoutManager

        layout.recyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(layout.recyclerView)

        adapter.itemClickListener = { position, view ->
            lifecycleScope.launch(Dispatchers.Default) {
                Manager2HomeView.initFromViewRect(
                    view,
                    window,
                    view.getTag(R.id.dn_theme_color) as Int? ?: Color.TRANSPARENT,
                )
                EventBus
                    .getDefault()
                    .post(MessageEvent.create(MessageType.Get2MainActivityAnim, position))

                withContext(Dispatchers.Main) {
                    if (Build.VERSION.SDK_INT >= 34) {
                        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
                    } else {
                        overridePendingTransition(0, 0)
                    }
                    finish()
                }
            }
        }

        adapter.itemLongClickListener = { _, _ ->
            viewModel.setSelectable(isSelectable = true)
        }

        adapter.addCityClickListener = {
            startActivity(Intent(this@ActivityManagerCity, ActivityAddCity::class.java))
        }

        layout.deleteBtn.setOnClickListener {
            if (adapter.getSelectCityNames().size == adapter.items.size) {
                toast("至少保留一个城市")
                return@setOnClickListener
            }
            viewModel.deleteCities(adapter.getSelectCityNames())
        }

        lifecycleScope.launch {
            viewModel.allCityWithWeather.collectLatest { it ->

                if (!hasDoEnterAnim) {
                    layout.recyclerView.itemAnimator = null
                    layout.recyclerView.doOnNextLayout {
                        layout.recyclerView.children.find { it.tag == intent.getStringExtra(ARG_CITY_NAME) }?.let { toView ->
                            hasDoEnterAnim = true
                            createInAnim(toView)
                            layout.recyclerView.itemAnimator = DefaultItemAnimator()
                        }
                    }
                }

                adapter.setItems(it)
            }
        }

        lifecycleScope.launch {
            viewModel.topTitle.collectLatest {
                layout.titleBar.title = it
            }
        }

        lifecycleScope.launch {
            viewModel.isSelectable.collectLatest {
                adapter.isSelectMode = it
                itemTouchHelper.isDrag = it
                if (it) {
                    Timber.tag("ManagerCity").d("show delete button")
                    layout.deleteBtn.show()
                } else {
                    Timber.tag("ManagerCity").d("hide delete button")
                    layout.deleteBtn.hide()
                }
            }
        }
        layout.titleBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (adapter.isSelectMode) {
                        viewModel.setSelectable(false)
                        return
                    }
                    backToMain()
                }
            },
        )
    }

    @Suppress("DEPRECATION")
    private fun backToMain() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            overridePendingTransition(0, 0)
        }
        val firstItemView = layout.recyclerView.findViewHolderForAdapterPosition(0)?.itemView
        if (firstItemView != null) {
            firstItemView.performClick()
            return
        }
        finish()
    }

    private fun createInAnim(toView: View) {
        home2ManagerView.bindEndView(toView, layout)
        doOnMainThreadIdle({
            startInAnim()
        }, 200)
    }

    // 开始入场动画
    private fun startInAnim() {
        home2ManagerView.startAnim()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_manager_city, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_city) {
            startActivity(Intent(this@ActivityManagerCity, ActivityAddCity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
