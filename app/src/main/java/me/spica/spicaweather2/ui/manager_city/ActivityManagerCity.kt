package me.spica.spicaweather2.ui.manager_city

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import me.spica.spicaweather2.tools.startActivityWithAnimation
import me.spica.spicaweather2.ui.add_city.ActivityAddCity
import me.spica.spicaweather2.ui.main.ActivityMain
import me.spica.spicaweather2.view.Manger2HomeView
import me.spica.spicaweather2.view.view_group.ActivityManagerCityLayout
import org.greenrobot.eventbus.EventBus
import rikka.material.app.MaterialActivity

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

        adapter.deleteCityClickListener = { cityWithWeather ->
            viewModel.deleteCity(cityWithWeather.city)
        }

        adapter.itemClickListener = { position, view ->
            lifecycleScope.launch(Dispatchers.Default) {
                Manger2HomeView.initFromViewRect(view, window)
                EventBus.getDefault().post(MessageEvent.create(MessageType.Get2MainActivityAnim, position))
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }

        adapter.addCityClickListener = {
            startActivityWithAnimation<ActivityAddCity>(
                enterResId = android.R.anim.fade_in,
                exitResId = android.R.anim.fade_out
            ) { }
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

        lifecycleScope.launch {
            viewModel.allCityWithWeather.collectLatest {
                adapter.setItems(it)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val firstItemView = layout.recyclerView.findViewHolderForAdapterPosition(0)?.itemView
            if (firstItemView != null) {
                firstItemView.performClick()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
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
            startActivityWithAnimation<ActivityAddCity>(
                enterResId = android.R.anim.fade_in,
                exitResId = android.R.anim.fade_out
            ) { }
        }
        return super.onOptionsItemSelected(item)
    }
}
