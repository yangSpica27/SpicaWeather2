package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.content.res.ColorStateList
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.toColorInt
import androidx.core.view.marginTop
import me.spica.spicaweather2.R

// / 生活指数item布局
class TipsItemLayout(
  context: Context,
) : AViewGroup(context) {
  private val titleText =
    AppCompatTextView(context).apply {
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium)
      setTextColor(context.getColor(R.color.white))
    }

  private val descText =
    AppCompatTextView(context).apply {
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
      layoutParams =
        MarginLayoutParams(
          MarginLayoutParams.WRAP_CONTENT,
          MarginLayoutParams.WRAP_CONTENT,
        ).apply {
          topMargin = 8.dp
        }
      setTextColor(context.getColor(R.color.white))
    }

  fun setData(
    title: String,
    desc: String,
  ) {
    titleText.text = title
    descText.text = desc
  }

  init {
    setPadding(
      14.dp,
      12.dp,
      14.dp,
      12.dp,
    )
    addView(titleText)
    addView(descText)
    setBackgroundResource(R.drawable.bg_round)
    backgroundTintList = ColorStateList.valueOf("#A6000000".toColorInt())
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    descText.autoMeasure()
    titleText.autoMeasure()
    setMeasuredDimension(
      resolveSize(
        Math.max(
          titleText.measuredWidthWithMargins,
          descText.measuredHeightWithMargins,
        ),
        widthMeasureSpec,
      ),
      resolveSize(
        (titleText.measuredHeightWithMargins + descText.measuredHeightWithMargins) + paddingTop + paddingBottom,
        heightMeasureSpec,
      ),
    )
  }

  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int,
  ) {
    titleText.layout(
      paddingLeft,
      paddingTop,
    )
    descText.layout(
      paddingLeft,
      titleText.bottom + descText.marginTop,
    )
  }
}
