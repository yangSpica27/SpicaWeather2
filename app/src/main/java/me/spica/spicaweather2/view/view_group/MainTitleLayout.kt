package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.getStatusBarHeight

class MainTitleLayout(
  context: Context,
  attributeSet: AttributeSet? = null,
) : AViewGroup(context, attributeSet) {
  val titleTextView =
    AppCompatTextView(context).apply {
      layoutParams =
        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
      text = "--"
      setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
      typeface =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          Typeface.create(Typeface.DEFAULT, 500, false)
        } else {
          Typeface.DEFAULT
        }
    }

  val plusBtn =
    AppCompatTextView(context).apply {
      layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
      setText(R.string.add)
      setPadding(12.dp)
      setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
      textSize = 18f
      typeface =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          Typeface.create(Typeface.DEFAULT, 500, false)
        } else {
          Typeface.DEFAULT
        }
    }

  init {
    setPadding(14.dp, 12.dp + context.getStatusBarHeight(), 14.dp, 12.dp)
    addView(titleTextView)
    addView(plusBtn)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    titleTextView.autoMeasure()
    plusBtn.autoMeasure()
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(
        titleTextView.measuredHeightWithMargins +
            paddingBottom +
            paddingTop,
        heightMeasureSpec,
      ),
    )
  }

  override fun onLayout(
    p0: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int,
  ) {
    titleTextView.layout(
      titleTextView.toHorizontalCenter(this),
      paddingTop,
    )
    plusBtn.layout(
      paddingRight,
      plusBtn.toViewVerticalCenter(titleTextView) + plusBtn.marginTop,
      true,
    )
  }
}
