package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorInflater
import android.content.Context
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.getCompatDrawable

// 湿度描述卡片
class DescCardViewLayout(
  context: Context,
) : AViewGroup(context) {
  // 标题
  private val titleTextView =
    AppCompatTextView(context).apply {
      layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
      setSingleLine()
      setPadding(12.dp, 12.dp, 12.dp, 8.dp)
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
      setTextColor(context.getColor(R.color.text_color_white))
      textSize = 15f
      text = "--"
    }

  // 副标题
  private val subTitleTextView: AppCompatTextView =
    AppCompatTextView(context).apply {
      layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
      setSingleLine()
      text = "--"
      setPadding(12.dp, 0, 12.dp, 0)
      setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
      setTextColor(context.getColor(R.color.white))
      textSize = 20f
    }

  // 进度条
  private val descProgressLineView =
    DescProgressLineView(context).apply {
      layoutParams = LayoutParams(MATCH_PARENT, 12.dp)
    }

  // 建议描述文本
  private val suggestTextView =
    AppCompatTextView(context).apply {
      layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
      maxLines = 2
      text = "--"
      setPadding(12.dp, 0, 12.dp, 12.dp)
      setTextColor(context.getColor(R.color.window_background))
      textSize = 14.5f
    }

  private var animDelay = 1000

  fun setAnimDelay(delay: Int) {
    animDelay = delay
  }

  fun getAnimDelay(): Int {
    return animDelay
  }

  init {
    addView(titleTextView)
    addView(subTitleTextView)
    addView(descProgressLineView)
    addView(suggestTextView)
    setBackgroundResource(R.drawable.bg_card)
    stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.touch_raise)
    isClickable = true
  }

  fun setShowData(
    @DrawableRes icon: Int, // 图标
    title: String, // 标题
    subTitle: String, // 副标题
    suggest: String, // 建议文本
    progress: Double, // 进度
    @IntRange(from = 1, to = 4) mode: Int, // 1:紫外线 2：湿度 3：体感温度 4：日出日落
  ) {
    val spannableString = SpannableString("  $title")
    val imageSpan =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ImageSpan(
          context.getCompatDrawable(icon)?.apply {
            setBounds(0, 0, 16.dp, 16.dp)
            setTint(context.getColor(R.color.text_color_white))
          }!!,
          ImageSpan.ALIGN_CENTER,
        )
      } else {
        ImageSpan(
          context.getCompatDrawable(icon)?.apply {
            setBounds(0, 0, 16.dp, 16.dp)
            setTint(context.getColor(R.color.text_color_white))
          }!!,
        )
      }
    spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    titleTextView.text = spannableString
    subTitleTextView.text =
      HtmlCompat.fromHtml(
        "<b>$subTitle</b>",
        HtmlCompat.FROM_HTML_MODE_LEGACY,
      )
    suggestTextView.text = suggest
    descProgressLineView.setProgress(progress.toFloat(), mode)
  }

  fun resetAnim() {
    titleTextView.translationY = -12.dp.toFloat()
    titleTextView.alpha = 0f
    subTitleTextView.translationY = -12.dp.toFloat()
    subTitleTextView.alpha = 0f
    suggestTextView.translationY = -12.dp.toFloat()
    suggestTextView.alpha = 0f
  }

  fun doShowEnterAnimator() {
    titleTextView
      .animate()
      .alpha(1f)
      .translationY(0f)
      .setDuration(350)
      .setStartDelay(animDelay.toLong())
      .withLayer()
      .start()
    subTitleTextView
      .animate()
      .alpha(1f)
      .setDuration(550)
      .translationY(0f)
      .setStartDelay(animDelay.toLong())
      .withLayer()
      .start()
    suggestTextView
      .animate()
      .alpha(1f)
      .setDuration(850)
      .translationY(0f)
      .setStartDelay(animDelay.toLong())
      .withLayer()
      .start()
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    titleTextView.measure(widthMeasureSpec, titleTextView.defaultHeightMeasureSpec(this))
    subTitleTextView.measure(widthMeasureSpec, subTitleTextView.defaultHeightMeasureSpec(this))
    suggestTextView.measure(widthMeasureSpec, suggestTextView.defaultHeightMeasureSpec(this))
    descProgressLineView.measure(
      widthMeasureSpec,
      (
          MeasureSpec.getSize(widthMeasureSpec) * 1.5.toInt() - titleTextView.measuredHeightWithMargins -
              subTitleTextView.measuredHeightWithMargins -
              suggestTextView.measuredHeightWithMargins - paddingTop - paddingBottom
          ).toExactlyMeasureSpec(),
    )
    setMeasuredDimension(
      measuredWidth,
      measuredWidth,
    )
  }

  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int,
  ) {
    titleTextView.layout(0 + titleTextView.marginLeft, 0)
    subTitleTextView.layout(0, titleTextView.bottom + subTitleTextView.marginTop)
    descProgressLineView.layout(0, subTitleTextView.bottom + descProgressLineView.marginTop)
    suggestTextView.layout(0, descProgressLineView.bottom)
  }
}
