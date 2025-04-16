package me.spica.spicaweather2.view.view_group

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import androidx.core.view.updateMargins
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.getColorWithAlpha

class CurrentWeatherLayout(
  context: Context,
) : AViewGroup(context = context) {
  @SuppressLint("SetTextI18n")
  private val tempTextView =
    AppCompatTextView(context).apply {
      setTextAppearance(
        context,
        com.google.android.material.R.style.TextAppearance_Material3_HeadlineLarge
      )
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        )
      setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
      includeFontPadding = false
    }

  private val descTextView =
    AppCompatTextView(context).apply {
      setTextAppearance(
        context,
        com.google.android.material.R.style.TextAppearance_Material3_LabelLarge
      )
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
          it.updateMargins(left = 15.dp)
        }
      setTextColor("#D6E8F4".toColorInt())
      setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21f)
      includeFontPadding = false
    }

  val dotIndicator =
    WormDotsIndicator(context).apply {
      layoutParams =
        LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT,
        ).also {
          it.topMargin = 12.dp
        }
      this.setPadding(0)
      this.setDotIndicatorColor(ContextCompat.getColor(context, R.color.text_color_white))
      this.setStrokeDotsIndicatorColor(
        ContextCompat.getColor(
          context,
          R.color.white,
        ),
      )
    }

  init {
    isFocusable = false
    isClickable = false
    addView(tempTextView)
    addView(descTextView)
    layoutParams =
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      )
    addView(dotIndicator)
    setPadding(24.dp, 24.dp, 24.dp, 24.dp)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    tempTextView.autoMeasure()
    descTextView.autoMeasure()
    dotIndicator.autoMeasure()
    setMeasuredDimension(
      resolveSize(
        tempTextView.measuredWidth.coerceAtLeast(descTextView.measuredWidth),
        widthMeasureSpec,
      ),
      tempTextView.measuredHeight + descTextView.measuredHeight + dotIndicator.measuredHeightWithMargins + paddingTop + paddingBottom,
    )
  }

  override fun onLayout(
    p0: Boolean,
    p1: Int,
    p2: Int,
    p3: Int,
    p4: Int,
  ) {
    tempTextView.layout(
      paddingLeft,
      paddingTop,
    )
    descTextView.layout(
      paddingLeft + descTextView.marginLeft,
      tempTextView.bottom,
    )
    dotIndicator.layout(
      paddingLeft + descTextView.marginLeft,
      descTextView.bottom + dotIndicator.marginTop,
    )
  }

  private var isFirstInit = false

  init {
    visibility = View.INVISIBLE
  }

  @Synchronized
  fun bindData(weather: Weather) {
    if (isFirstInit) {
      setText(weather)
      isFirstInit = false
    } else {
      progressAnim.removeAllUpdateListeners()
      progressAnim.addUpdateListener {
        if (progressAnim.animatedValue as Int >= 50) {
          setText(weather)
          progressAnim.removeAllListeners()
        }
      }
      translateAnimation.cancel()
      translateAnimation.start()
      if (visibility == View.INVISIBLE) {
        visibility = View.VISIBLE
      }
    }
  }

  private fun setText(weather: Weather) {
    tempTextView.text =
      SpannableStringBuilder()
        .append(
          "${weather.hourlyWeather[0].temp}",
          AbsoluteSizeSpan(170.dp),
          Spannable.SPAN_INCLUSIVE_INCLUSIVE,
        ).append(
          "℃",
          AbsoluteSizeSpan(30.dp),
          Spannable.SPAN_INCLUSIVE_INCLUSIVE,
        )

    descTextView.text = HtmlCompat.fromHtml(
      "${weather.todayWeather.weatherName},降水概率&nbsp<b>${weather.hourlyWeather[0].pop}%<b/>",
      HtmlCompat.FROM_HTML_MODE_LEGACY,
    )
    tempTextView.post {
      val mLinearGradient =
        LinearGradient(
          0f,
          0f,
          0f,
          tempTextView.height * 1f,
          intArrayOf(
            Color.WHITE,
            getColorWithAlpha(0.9f, Color.WHITE),
            getColorWithAlpha(0.5f, Color.WHITE),
          ),
          floatArrayOf(
            0f,
            0.5f,
            1f,
          ),
          Shader.TileMode.CLAMP,
        )
      tempTextView.paint.setShader(mLinearGradient)
      tempTextView.invalidate()
    }
  }

  // 用于获取动画进度
  private val progressAnim: ValueAnimator = ValueAnimator.ofInt(0, 100)

  // 切换显示内容的时候的动画
  private val translateAnimation by lazy {

    val animator1: ObjectAnimator =
      ObjectAnimator.ofFloat(
        descTextView,
        "alpha",
        1f,
        0f,
        1f,
      )

    val animator2: ObjectAnimator =
      ObjectAnimator.ofFloat(
        descTextView,
        "translationY",
        0f,
        12f,
        0f,
      )

    val animator3: ObjectAnimator =
      ObjectAnimator.ofFloat(
        tempTextView,
        "alpha",
        1f,
        0f,
        1f,
      )

    val animator4: ObjectAnimator =
      ObjectAnimator.ofFloat(
        tempTextView,
        "translationY",
        0f,
        12f,
        0f,
      )

    // 描述文本动画
    val set1 =
      AnimatorSet().apply {
        play(animator1).with(animator2)
      }

    // 温度文本动画
    val set2 =
      AnimatorSet().apply {
        play(animator3).with(animator4)
      }

    val set = AnimatorSet()

    // 设置动画的基础属性
    set.play(set1).with(set2).with(progressAnim) // 两个动画同时开始

    set.setDuration(750) // 播放时长
    set.startDelay = 50 // 延迟播放
    set.interpolator = DecelerateInterpolator() // 设置插值器
    return@lazy set
  }
}
