package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.core.text.HtmlCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.common.HomeCardType
import me.spica.spicaweather2.common.getThemeColor
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.view.AirCircleProgressView
import me.spica.spicaweather2.view.weather_detail_card.SpicaWeatherCard
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("SameParameterValue")
@SuppressLint("SetTextI18n")
class AirCardLayout(
  context: Context,
) : AViewGroup(context),
  SpicaWeatherCard {
  // 标题文字
  private val titleText =
    AppCompatTextView(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
          it.updateMargins(
            left = 14.dp,
            top = 12.dp,
          )
        }
      setTextAppearance(
        context,
        com.google.android.material.R.style.TextAppearance_Material3_TitleMedium
      )
      typeface = Typeface.DEFAULT_BOLD
      text = "空气质量"
    }

  // 进度条
  private val airCircleProgressView =
    AirCircleProgressView(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
          it.updateMargins(
            left = 14.dp,
            top = 12.dp,
          )
        }
    }

  private val tvC0Title =
    createTitleTextView("一氧化碳").apply {
      updateLayoutParams<MarginLayoutParams> { topMargin = 12.dp }
    }
  private val tvC0Value = createValueTextView("--微克/m³")
  private val tvSo2Title = createTitleTextView("二氧化硫")
  private val tvSo2Value = createValueTextView("--微克/m³")
  private val tvNo2Title = createTitleTextView("二氧化氮")
  private val tvNo2Value = createValueTextView("--微克/m³")
  private val tvPm25Title = createTitleTextView("PM2.5")
  private val tvPm25Value = createValueTextView("--微克/m³")

  private fun createTitleTextView(title: String): AppCompatTextView =
    AppCompatTextView(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
          it.updateMargins(
            left = 12.dp,
          )
        }
      setTextAppearance(
        context,
        com.google.android.material.R.style.TextAppearance_Material3_BodyMedium
      )
      typeface = Typeface.DEFAULT_BOLD
      setTextColor(context.getColor(R.color.textColorPrimary))
      text = title
    }

  private fun createValueTextView(value: String = ""): AppCompatTextView =
    AppCompatTextView(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
          it.updateMargins(
            right = 14.dp,
          )
        }
      setTextAppearance(
        context,
        com.google.android.material.R.style.TextAppearance_Material3_BodyMedium
      )
      setTextColor(context.getColor(R.color.textColorPrimaryHint))
      text = value
    }

  init {
    stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.touch_raise)
    isClickable = true
    setBackgroundResource(R.drawable.bg_card)
    setPadding(0, 0, 0, 12.dp)
    addView(titleText)
    addView(airCircleProgressView)
    addView(tvC0Title)
    addView(tvC0Value)
    addView(tvNo2Title)
    addView(tvNo2Value)
    addView(tvSo2Title)
    addView(tvSo2Value)
    addView(tvPm25Title)
    addView(tvPm25Value)
    val lp =
      MarginLayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      ).also {
        it.updateMargins(left = 14.dp, right = 14.dp)
      }
    layoutParams = lp
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    titleText.autoMeasure()
    airCircleProgressView.measure(
      (measuredWidth / 2 - airCircleProgressView.marginLeft).toExactlyMeasureSpec(),
      airCircleProgressView.defaultHeightMeasureSpec(this),
    )
    tvC0Title.autoMeasure()
    tvC0Value.autoMeasure()
    tvNo2Title.autoMeasure()
    tvNo2Value.autoMeasure()
    tvSo2Title.autoMeasure()
    tvSo2Value.autoMeasure()
    tvPm25Title.autoMeasure()
    tvPm25Value.autoMeasure()
    setMeasuredDimension(
      resolveSize(measuredWidth, widthMeasureSpec),
      resolveSize(
        Math.max(
          airCircleProgressView.measuredHeightWithMargins +
              titleText.measuredHeightWithMargins,
          tvNo2Title.measuredHeightWithMargins +
              tvC0Title.measuredHeightWithMargins +
              tvPm25Title.measuredHeightWithMargins +
              tvSo2Title.measuredHeightWithMargins +
              titleText.measuredHeightWithMargins +
              paddingBottom + paddingTop,
        ),
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
    titleText.layout(titleText.marginLeft, titleText.marginTop)
    airCircleProgressView.layout(
      airCircleProgressView.marginLeft,
      titleText.bottom + airCircleProgressView.marginTop,
    )

    val titleHeight =
      tvC0Title.height +
          tvPm25Title.height +
          tvSo2Title.height +
          titleText.height

    val progressHeight = airCircleProgressView.height

    val itemSpacer = Math.max(0, progressHeight - titleHeight - paddingBottom) / 4

    tvC0Title.layout(
      width / 2 + tvC0Title.marginLeft,
      airCircleProgressView.top + tvC0Title.marginTop,
    )

    tvC0Value.layout(
      tvC0Value.marginRight,
      tvC0Value.toViewVerticalCenter(tvC0Title),
      true,
    )

    tvSo2Title.layout(
      width / 2 + tvSo2Title.marginLeft,
      tvC0Title.bottom + itemSpacer,
    )

    tvSo2Value.layout(
      tvSo2Value.marginRight,
      tvSo2Value.toViewVerticalCenter(tvSo2Title),
      true,
    )

    tvNo2Title.layout(
      width / 2 + tvNo2Title.marginLeft,
      tvSo2Title.bottom + itemSpacer,
    )

    tvNo2Value.layout(
      tvNo2Value.marginRight,
      tvNo2Value.toViewVerticalCenter(tvNo2Title),
      true,
    )

    tvPm25Title.layout(
      width / 2 + tvPm25Title.marginLeft,
      tvNo2Title.bottom + itemSpacer,
    )

    tvPm25Value.layout(
      tvPm25Value.marginRight,
      tvPm25Value.toViewVerticalCenter(tvPm25Title),
      true,
    )
  }

  override var animatorView: View = this

  override var enterAnim: AnimatorSet = AnimatorSet()
  override var index: Int = HomeCardType.AIR.code
  override var hasInScreen: AtomicBoolean = AtomicBoolean(false)

  override fun bindData(weather: Weather) {
    val themeColor = weather.getWeatherType().getThemeColor()
    airCircleProgressView.bindProgress(weather.air.aqi, weather.air.category)
    titleText.setTextColor(themeColor)
    tvC0Value.text =
      HtmlCompat.fromHtml("${weather.air.co}<b>微克/m³</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
//        tvC0Value.text = "${weather.air.co}微克/m³"
    tvNo2Value.text =
      HtmlCompat.fromHtml(
        "${weather.air.no2}<b>微克/m³</b>",
        HtmlCompat.FROM_HTML_MODE_LEGACY,
      )
    tvPm25Value.text =
      HtmlCompat.fromHtml(
        "${weather.air.pm2p5}<b>微克/m³</b>",
        HtmlCompat.FROM_HTML_MODE_LEGACY,
      )
    tvSo2Value.text =
      HtmlCompat.fromHtml(
        "${weather.air.so2}<b>微克/m³</b>",
        HtmlCompat.FROM_HTML_MODE_LEGACY,
      )
    airCircleProgressView.postInvalidate()
  }

  private val valueTextAnim =
    ObjectAnimator.ofFloat(this, "doValueTextAnim", 0f, 1f).apply {
      duration = 1250
      interpolator = DecelerateInterpolator(1.2f)
    }

  private fun setDoValueTextAnim(progress: Float) {
    tvC0Value.alpha = progress
    tvNo2Value.alpha = progress
    tvPm25Value.alpha = progress
    tvSo2Value.alpha = progress

    tvC0Title.alpha = progress
    tvNo2Title.alpha = progress
    tvPm25Title.alpha = progress
    tvSo2Title.alpha = progress

    tvNo2Title.translationY = (1 - progress) * (-4).dp
    tvPm25Title.translationY = (1 - progress) * (-4).dp
    tvSo2Title.translationY = (1 - progress) * (-4).dp
    tvC0Title.translationY = (1 - progress) * (-4).dp

    tvC0Value.translationX = (1 - progress) * (-4).dp
    tvNo2Value.translationX = (1 - progress) * (-4).dp
    tvPm25Value.translationX = (1 - progress) * (-4).dp
    tvSo2Value.translationX = (1 - progress) * (-4).dp
  }

  override fun resetAnim() {
    super.resetAnim()
    setDoValueTextAnim(0f)
  }

  override fun startEnterAnim() {
    super.startEnterAnim()
    enterAnim.doOnEnd {
      airCircleProgressView.startAnim()
      valueTextAnim.start()
    }
  }
}
