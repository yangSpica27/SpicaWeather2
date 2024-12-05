package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import me.spica.spicaweather2.R

class RefreshViewLayout(
  context: Context,
) : AViewGroup(context) {
  init {
    layoutParams =
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      )
  }

  val loadingImageView =
    AppCompatImageView(context).apply {
      setImageResource(R.drawable.ic_loading_sun)
      layoutParams =
        LayoutParams(60.dp, 60.dp).apply {
          topMargin = 12.dp
          bottomMargin = 8.dp
        }
    }

  val tipText =
    AppCompatTextView(context).apply {
      setTextColor(Color.WHITE)
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium)
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
      gravity = Gravity.CENTER
    }

  init {
    addView(loadingImageView)
    addView(tipText)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    loadingImageView.autoMeasure()
    tipText.autoMeasure()
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(
        loadingImageView.measuredHeightWithMargins + tipText.measuredHeightWithMargins,
        heightMeasureSpec,
      ),
    )
  }

  override fun onLayout(
    changed: Boolean,
    p1: Int,
    p2: Int,
    p3: Int,
    p4: Int,
  ) {
    loadingImageView.layout(
      loadingImageView.toHorizontalCenter(this),
      paddingTop + loadingImageView.marginTop,
    )
    tipText.layout(
      0,
      loadingImageView.bottom + loadingImageView.marginBottom,
    )
  }
}
