package me.spica.spicaweather2.ui.manager_city

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.android.material.transition.platform.MaterialContainerTransform
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
import me.spica.spicaweather2.tools.startActivityWithAnimation
import me.spica.spicaweather2.tools.toast
import me.spica.spicaweather2.ui.add_city.ActivityAddCity
import me.spica.spicaweather2.ui.main.ActivityMain
import me.spica.spicaweather2.view.Manager2HomeView
import me.spica.spicaweather2.view.view_group.ActivityManagerCityLayout
import org.greenrobot.eventbus.EventBus
import rikka.material.app.MaterialActivity
import timber.log.Timber

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

    private val adapter = ManagerCityAdapter()

    private val viewModel by viewModels<CityManagerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        setContentView(layout)
        init()
    }

    private fun init() {
        setSupportActionBar(layout.titleBar)
        layout.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
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

        layout.recyclerView.adapter = adapter

        adapter.itemClickListener = { position, view ->
            lifecycleScope.launch(Dispatchers.Default) {
                Manager2HomeView.initFromViewRect(view, window)
                EventBus.getDefault()
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
           startActivity(Intent(this@ActivityManagerCity,ActivityAddCity::class.java))
        }

        if (ActivityMain.screenBitmap != null) {
            layout.transformerImageView.setImageBitmap(ActivityMain.screenBitmap)
        }

        layout.recyclerView.addOnChildAttachStateChangeListener(
            object : RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    if (view.tag?.toString() == intent.getStringExtra(ARG_CITY_NAME)) {
                        startTransformerAnim(view)
                        layout.recyclerView.removeOnChildAttachStateChangeListener(this)
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) = Unit
            })

        layout.deleteBtn.setOnClickListener {
            if (adapter.getSelectCityNames().size == adapter.items.size) {
                toast("至少保留一个城市")
                return@setOnClickListener
            }
            viewModel.deleteCities(adapter.getSelectCityNames())
        }

        lifecycleScope.launch {
            viewModel.allCityWithWeather.collectLatest {
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
                if (it) {
                    Timber.tag("ManagerCity").d("show delete button")
                    layout.deleteBtn.show()
                } else {
                    Timber.tag("ManagerCity").d("hide delete button")
                    layout.deleteBtn.hide()
                }
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (adapter.isSelectMode) {
                    viewModel.setSelectable(false)
                    return
                }
                backToMain()
            }
        })

    }


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


    private fun startTransformerAnim(toView: View) {
        val container = findViewById<ViewGroup>(android.R.id.content)
        toView.visibility = View.INVISIBLE
        val transform = MaterialContainerTransform().apply {
            startView = layout.transformerImageView
            endView = toView
            addTarget(endView)
            scrimColor = Color.TRANSPARENT
            containerColor = Color.TRANSPARENT
            duration = 850
//            interpolator = DecelerateInterpolator(1.2f)
//            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
        }

        transform.doOnStart {
            toView.visibility = View.INVISIBLE
        }

        transform.doOnEnd {
            layout.removeView(layout.transformerImageView)
            toView.visibility = View.VISIBLE
        }

        doOnMainThreadIdle({
            TransitionManager.beginDelayedTransition(container, transform)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_manager_city, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_city) {
            startActivity(Intent(this@ActivityManagerCity,ActivityAddCity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
