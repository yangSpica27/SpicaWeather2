package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.getStatusBarHeight
import me.spica.spicaweather2.view.search_edit.SearchBarLayout

class ActivityAddCityLayout(
  context: Context,
) : AViewGroup(context) {
  val searchBarLayout =
    SearchBarLayout(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
          this.topMargin = 12.dp +
              context.getStatusBarHeight()
          this.bottomMargin = 12.dp
          leftMargin = 20.dp
          rightMargin = 20.dp
        }
    }

  val recyclerView =
    RecyclerView(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        )
      clipToPadding = false
    }

  init {
    setBackgroundResource(R.color.white)
    addView(searchBarLayout)
    addView(recyclerView)
    layoutParams =
      LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    searchBarLayout.autoMeasure()
    recyclerView.measure(
      measuredWidth.toExactlyMeasureSpec(),
      (measuredHeight - searchBarLayout.measuredHeightWithMargins).toExactlyMeasureSpec(),
    )
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(measuredHeight, heightMeasureSpec),
    )
  }

  override fun onLayout(
    changed: Boolean,
    p1: Int,
    p2: Int,
    p3: Int,
    p4: Int,
  ) {
    searchBarLayout.layout(
      searchBarLayout.marginLeft,
      paddingTop + searchBarLayout.marginTop,
    )
    recyclerView.layout(
      0,
      searchBarLayout.bottom + searchBarLayout.marginBottom,
    )
  }
}
