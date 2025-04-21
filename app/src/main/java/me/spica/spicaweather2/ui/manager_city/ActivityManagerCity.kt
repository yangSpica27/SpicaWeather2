package me.spica.spicaweather2.ui.manager_city

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.size.Scale
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.spica.spicaweather2.R
import me.spica.spicaweather2.base.BaseActivity
import me.spica.spicaweather2.persistence.repository.CityRepository
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
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.contracts.ExperimentalContracts

/**
 * 城市管理页面
 */
@AndroidEntryPoint
class ActivityManagerCity : BaseActivity() {
  companion object {
    const val ARG_CITY_NAME = "arg_position"
  }

  private val layout by lazy {
    ActivityManagerCityLayout(this)
  }


  // 是否已经执行了进入动画
  @Volatile
  private var hasDoEnterAnim = false

  // 从主页到当前页面的动画遮罩
  private val home2ManagerView by lazy {
    Home2ManagerView(this)
  }

  // 列表适配器
  private val adapter = ManagerCityAdapter()

  private val viewModel by viewModels<CityManagerViewModel>()


  // 长按排序TouchHelper
  private val itemTouchHelper =
    CityItemTouchHelper(
      isDrag = false,
      onMove = { viewHolder, target ->
        // 当列表发生移动时触发
        // 1. 更新数据
        // 2. 刷新UI
        Collections.swap(
          adapter.items,
          viewHolder.absoluteAdapterPosition,
          target.absoluteAdapterPosition,
        )
        adapter.notifyItemMoved(
          viewHolder.absoluteAdapterPosition,
          target.absoluteAdapterPosition,
        )
        viewModel.moveCity(
          adapter.items[viewHolder.absoluteAdapterPosition].city,
          adapter.items[target.absoluteAdapterPosition].city,
        )
      },
    )

  @Inject
  lateinit var cityRepository: CityRepository

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
    // 设置状态栏颜色
    WindowCompat.getInsetsController(this.window, window.decorView).apply {
      isAppearanceLightStatusBars = true
    }
    window.decorView.setBackgroundColor(Color.WHITE)
    // 设置分割线
    dividerBuilder()
      .showLastDivider()
      .showFirstDivider()
      .asSpace()
      .size(12.dp.toInt())
      .build()
      .addTo(layout.recyclerView)

    // 将遮罩添加到rootview
    home2ManagerView.attachToRootView()

    val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    layoutManager.initialPrefetchItemCount = 5

    layout.recyclerView.layoutManager = layoutManager
    layout.recyclerView.setHasFixedSize(true)
    layout.recyclerView.adapter = adapter
    itemTouchHelper.attachToRecyclerView(layout.recyclerView)

    // 列表点击事件
    adapter.itemClickListener = { position, view ->
      Manager2HomeView.initFromViewRect(
        view,
        window,
        view.getTag(R.id.dn_theme_color) as Int? ?: Color.TRANSPARENT,
      )

      // 绑定EventBus
      EventBus
        .getDefault()
        .post(MessageEvent.create(MessageType.Get2MainActivityAnim, position))


      // 删除动画
      if (Build.VERSION.SDK_INT >= 34) {
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
      } else {
        overridePendingTransition(0, 0)
      }
      finish()
    }

    // 列表长按事件
    adapter.itemLongClickListener = { _, _ ->
      viewModel.setSelectable(isSelectable = true)
    }

    // 点击添加城市按钮
    adapter.addCityClickListener = {
      startActivity(Intent(this@ActivityManagerCity, ActivityAddCity::class.java))
    }

    // 点击删除城市
    layout.deleteBtn.setOnClickListener {
      if (adapter.getSelectCityNames().size == adapter.itemCount) {
        toast("至少保留一个城市")
        return@setOnClickListener
      }
      viewModel.deleteCities(adapter.getSelectCityNames())
    }
    // 记录开始加载数据的时间 用于计算动画加载时间
    val startTime = System.currentTimeMillis()

    lifecycleScope.launch(Dispatchers.Default) {
      layout.recyclerView.itemAnimator = null
      val data = cityRepository.getAllCityWithWeather()
      runOnUiThread {
        layout.recyclerView.doOnNextLayout {
          layout.recyclerView.children.find { it.tag == intent.getStringExtra(ARG_CITY_NAME) }
            ?.let { toView ->
              createInAnim(toView)
              layout.recyclerView.itemAnimator = DefaultItemAnimator()
            }
          val endTime = System.currentTimeMillis()
          Timber.tag("动画绑定耗时").d("init time:${endTime - startTime}")
        }
        adapter.setItems(data)
      }
      hasDoEnterAnim = true
    }


    // 订阅title 的变化
    lifecycleScope.launch {
      viewModel.topTitle.collect {
        layout.titleBar.title = it
      }
    }

    // 订阅所有城市的数据变化
    lifecycleScope.launch {
      viewModel.allCityWithWeather.collect {
        if (hasDoEnterAnim) {
          adapter.setItems(it)
        }
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
    // 设置返回键的点击事件
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

  // 回到主页
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

  // 创建入场动画
  @OptIn(ExperimentalContracts::class)
  private fun createInAnim(toView: View) {
    home2ManagerView.bindEndView(toView, layout)
    home2ManagerView.doOnPreDraw {
      val scaleAnimator = ScaleAnimation(
        0.925f,
        1.0f,
        0.925f,
        1f,
        home2ManagerView.endRect.centerX(),
        home2ManagerView.endRect.centerY()
      )
//    scaleAnimator.interpolator = DecelerateInterpolator(1.2f)
      scaleAnimator.duration = 350
      layout.startAnimation(scaleAnimator)
    }

    doOnMainThreadIdle({
      home2ManagerView.startAnim()
    }, 100)
  }


  // 创建菜单
  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_manager_city, menu)
    return super.onCreateOptionsMenu(menu)
  }

  // 菜单点击
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.add_city) {
      startActivity(Intent(this@ActivityManagerCity, ActivityAddCity::class.java))
    }
    return super.onOptionsItemSelected(item)
  }
}
