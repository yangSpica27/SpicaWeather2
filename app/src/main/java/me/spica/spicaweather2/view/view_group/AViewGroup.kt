package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

abstract class AViewGroup(
  context: Context,
  attributeSet: AttributeSet? = null,
) : ViewGroup(context, attributeSet) {
  protected fun View.defaultWidthMeasureSpec(parentView: ViewGroup): Int =
    when (layoutParams.width) {
      ViewGroup.LayoutParams.MATCH_PARENT -> parentView.measuredWidth.toExactlyMeasureSpec()
      ViewGroup.LayoutParams.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT.toAtMostMeasureSpec()
      0 -> throw IllegalAccessException("Need special treatment for $this")
      else -> layoutParams.width.toExactlyMeasureSpec()
    }

  protected fun View.defaultHeightMeasureSpec(parentView: ViewGroup): Int =
    when (layoutParams.height) {
      ViewGroup.LayoutParams.MATCH_PARENT -> parentView.measuredHeight.toExactlyMeasureSpec()
      ViewGroup.LayoutParams.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT.toAtMostMeasureSpec()
      0 -> throw IllegalAccessException("Need special treatment for $this")
      else -> layoutParams.height.toExactlyMeasureSpec()
    }

  protected fun Int.toExactlyMeasureSpec(): Int =
    MeasureSpec.makeMeasureSpec(this, MeasureSpec.EXACTLY)

  protected fun Int.toAtMostMeasureSpec(): Int =
    MeasureSpec.makeMeasureSpec(this, MeasureSpec.AT_MOST)

  protected fun Int.toUnspecifiedMeasureSpec(): Int =
    MeasureSpec.makeMeasureSpec(this, MeasureSpec.UNSPECIFIED)

  protected fun View.autoMeasure() {
    measure(
      this.defaultWidthMeasureSpec(parentView = this@AViewGroup),
      this.defaultHeightMeasureSpec(parentView = this@AViewGroup),
    )
  }

  protected fun View.toHorizontalCenter(parentView: ViewGroup): Int =
    (parentView.measuredWidth - measuredWidth) / 2

  protected fun View.toVerticalCenter(parentView: ViewGroup): Int =
    (parentView.measuredHeight - measuredHeight) / 2

  protected fun View.toViewHorizontalCenter(targetView: View): Int =
    targetView.left - (measuredWidth - targetView.measuredWidth) / 2

  protected fun View.toViewVerticalCenter(targetView: View): Int =
    targetView.top - (measuredHeight - targetView.measuredHeight) / 2

  protected fun View.layout(
    x: Int,
    y: Int,
    fromRight: Boolean = false,
  ) {
    if (!fromRight) {
      layout(x, y, x + measuredWidth, y + measuredHeight)
    } else {
      layout(this@AViewGroup.measuredWidth - x - measuredWidth, y)
    }
  }

  protected val Int.dp: Int get() = (this * resources.displayMetrics.density + 0.5f).toInt()
  protected val View.measuredWidthWithMargins get() = (measuredWidth + marginLeft + marginRight)
  protected val View.measuredHeightWithMargins get() = (measuredHeight + marginTop + marginBottom)

  protected class LayoutParams(
    width: Int,
    height: Int,
  ) : MarginLayoutParams(width, height)

  protected val MATCH_PARENT: Int = ViewGroup.LayoutParams.MATCH_PARENT

  protected val WRAP_CONTENT: Int = ViewGroup.LayoutParams.WRAP_CONTENT
}
