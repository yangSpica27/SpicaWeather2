package me.spica.spicaweather2.view.view_group

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.getStatusBarHeight

class ActivityManagerCityLayout(
  context: Context,
) : AViewGroup(context) {
  val titleBar =
    MaterialToolbar(context).apply {
      setNavigationIcon(R.drawable.ic_left)
      title = "城市管理"
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
      updatePadding(
        top = paddingTop + context.getStatusBarHeight(),
      )
      setBackgroundColor(ContextCompat.getColor(context, R.color.white))
      setNavigationOnClickListener {
        (context as Activity).finish()
      }
    }

  val recyclerView =
    RecyclerView(context).apply {
      layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        )
//        setBackgroundColor(Color.BLACK)
    }

  val deleteBtn =
    AppCompatButton(context).apply {
      setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Button)
      setCompoundDrawablesRelative(
        null,
        ContextCompat.getDrawable(context, R.drawable.ic_delete).apply {
          this?.setBounds(0, 0, 24.dp, 24.dp)
        },
        null,
        null,
      )
      setBackgroundColor(ContextCompat.getColor(context, R.color.white))
      updatePadding(top = 14.dp, bottom = 14.dp)
      gravity = Gravity.CENTER
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
      text = "删除"
      visibility = GONE
    }

  init {
    layoutParams =
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
      )
    addView(recyclerView)
    addView(titleBar)
    addView(deleteBtn)
    setBackgroundColor(Color.WHITE)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    titleBar.autoMeasure()
    recyclerView.measure(
      widthMeasureSpec,
      (measuredHeight - titleBar.height).toExactlyMeasureSpec(),
    )
    deleteBtn.autoMeasure()
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(measuredHeight, heightMeasureSpec),
    )
  }

  override fun onLayout(
    p0: Boolean,
    p1: Int,
    p2: Int,
    p3: Int,
    p4: Int,
  ) {
    titleBar.layout(0, 0)
    recyclerView.layout(0, titleBar.height)
    deleteBtn.layout(left, bottom - deleteBtn.measuredHeight)
  }
}
