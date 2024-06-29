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
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginLeft
import androidx.core.view.updateMargins
import me.spica.spicaweather2.R
import me.spica.spicaweather2.persistence.entity.weather.Weather
import me.spica.spicaweather2.tools.getColorWithAlpha


class TodayDescLayout(context: Context) : AViewGroup(context = context) {

    @SuppressLint("SetTextI18n")
    private val tempTextView = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_HeadlineLarge)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text = "--"
        setTextColor(Color.WHITE)
        includeFontPadding = false
    }

    private val descTextView = AppCompatTextView(context).apply {
        setTextAppearance(context, R.style.TextAppearance_Material3_LabelLarge)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.updateMargins(left = 15.dp)
        }
        text = "--"
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21f)
        includeFontPadding = false
    }


    init {
        isFocusable = false
        isClickable = false
        addView(tempTextView)
        addView(descTextView)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(24.dp, 24.dp, 24.dp, 24.dp)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        tempTextView.autoMeasure()
        descTextView.autoMeasure()
        setMeasuredDimension(
            resolveSize(
                tempTextView.measuredWidth.coerceAtLeast(descTextView.measuredWidth),
                widthMeasureSpec
            ),
            tempTextView.measuredHeight + descTextView.measuredHeight + paddingTop + paddingBottom
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        tempTextView.layout(
            paddingLeft,
            paddingTop
        )
        descTextView.layout(
            paddingLeft + descTextView.marginLeft,
            tempTextView.bottom,
        )
    }

    private var isFirstInit = false

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
            translateAnimation.start()
        }
    }


    private fun setText(weather: Weather) {

        tempTextView.text = SpannableStringBuilder()
            .append(
                "${weather.hourlyWeather[0].temp}",
                AbsoluteSizeSpan(170.dp),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            .append(
                "℃", AbsoluteSizeSpan(30.dp),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        val bottomText = StringBuilder()
        bottomText.append(weather.todayWeather.weatherName)
        bottomText.append("，")
        bottomText.append("降水概率")
        bottomText.append(weather.hourlyWeather[0].pop)
        bottomText.append("%")
//        if (weather.alerts.isNotEmpty()) {
//            bottomText.append("\n")
//            bottomText.append(weather.alerts[0].title)
//        }
        descTextView.text = bottomText
        tempTextView.post {
            val mLinearGradient = LinearGradient(
                0f, 0f, 0f,
                tempTextView.height * 1f,
                intArrayOf(
                    Color.WHITE,
                    getColorWithAlpha(0.9f, Color.WHITE),
                    getColorWithAlpha(0.5f, Color.WHITE),
                ),
                floatArrayOf(
                    0f, 0.5f, 1f
                ),
                Shader.TileMode.CLAMP
            )
            tempTextView.paint.setShader(mLinearGradient)
            tempTextView.invalidate()
        }
    }


    // 用于获取动画进度
    private val progressAnim: ValueAnimator = ValueAnimator.ofInt(0, 100)

    // 切换显示内容的时候的动画
    private val translateAnimation by lazy {


        val animator1: ObjectAnimator = ObjectAnimator.ofFloat(
            descTextView,
            "alpha", 1f,
            0f, 1f
        )

        val animator2: ObjectAnimator = ObjectAnimator.ofFloat(
            descTextView,
            "translationY", 0f,
            12f, 0f
        )

        val animator3: ObjectAnimator = ObjectAnimator.ofFloat(
            tempTextView,
            "alpha", 1f,
            0f, 1f
        )

        val animator4: ObjectAnimator = ObjectAnimator.ofFloat(
            tempTextView,
            "translationY", 0f,
            12f, 0f
        )

        // 描述文本动画
        val set1 = AnimatorSet().apply {
            play(animator1).with(animator2)
        }

        // 温度文本动画
        val set2 = AnimatorSet().apply {
            play(animator3).with(animator4)
        }

        val set = AnimatorSet()

        //设置动画的基础属性
        set.play(set1).with(set2).with(progressAnim)// 两个动画同时开始

        set.setDuration(550) // 播放时长
        set.interpolator = DecelerateInterpolator() // 设置插值器
        return@lazy set
    }

}
