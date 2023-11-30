package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import me.spica.spicaweather2.tools.getColorWithAlpha
import me.spica.spicaweather2.tools.getStatusBarHeight


class MainTitleLayout(context: Context, attributeSet: AttributeSet? = null) : AViewGroup(context, attributeSet) {


    val titleTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setTextAppearance(me.spica.spicaweather2.R.style.TextAppearance_Material3_TitleLarge)
        text = "城市名称"
        setTextColor(Color.WHITE)
    }


    val plusBtn = AppCompatImageView(context).apply {
        layoutParams = LayoutParams(24.dp, 24.dp)
        setImageDrawable(ContextCompat.getDrawable(context, me.spica.spicaweather2.R.drawable.ic_plus))
    }

    val dotIndicator = WormDotsIndicator(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.topMargin = 12.dp
        }
        this.setPadding(0)
        this.setDotIndicatorColor(ContextCompat.getColor(context, me.spica.spicaweather2.R.color.white))
        this.setStrokeDotsIndicatorColor(ContextCompat.getColor(context, me.spica.spicaweather2.R.color.white))
    }


    fun setBackgroundWhiteColor(progress: Float) {
        setBackgroundColor(getColorWithAlpha(progress, Color.WHITE))
        if (progress > 0.5) {
            val up = ContextCompat.getDrawable(context, me.spica.spicaweather2.R.drawable.ic_plus)!!
            val drawableUp = DrawableCompat.wrap(up)
            DrawableCompat.setTint(drawableUp, ContextCompat.getColor(context, me.spica.spicaweather2.R.color.textColorPrimary))
            plusBtn.setImageDrawable(drawableUp)
            dotIndicator.setDotIndicatorColor(ContextCompat.getColor(context, me.spica.spicaweather2.R.color.textColorPrimary))
            dotIndicator.setStrokeDotsIndicatorColor(ContextCompat.getColor(context, me.spica.spicaweather2.R.color.textColorPrimaryHint))
            titleTextView.setTextColor(ContextCompat.getColor(context, me.spica.spicaweather2.R.color.textColorPrimary))
        } else {
            val up = ContextCompat.getDrawable(context, me.spica.spicaweather2.R.drawable.ic_plus)!!
            val drawableUp = DrawableCompat.wrap(up)
            DrawableCompat.setTint(drawableUp, ContextCompat.getColor(context, me.spica.spicaweather2.R.color.white))
            plusBtn.setImageDrawable(drawableUp)
            dotIndicator.setDotIndicatorColor(ContextCompat.getColor(context, me.spica.spicaweather2.R.color.white))
            dotIndicator.setStrokeDotsIndicatorColor(ContextCompat.getColor(context, me.spica.spicaweather2.R.color.white))
            titleTextView.setTextColor(Color.WHITE)
        }
    }

    init {
        alpha = .85f
        setPadding(14.dp, 12.dp + context.getStatusBarHeight(), 14.dp, 12.dp)
        addView(titleTextView)
        addView(plusBtn)
        addView(dotIndicator)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        titleTextView.autoMeasure()
        plusBtn.measure(24.dp.toExactlyMeasureSpec(), 24.dp.toExactlyMeasureSpec())
        dotIndicator.autoMeasure()
        setMeasuredDimension(
            measuredWidth,
            titleTextView.measuredHeightWithMargins
                + dotIndicator.measuredHeightWithMargins
                + paddingBottom
                + paddingTop
        )
    }


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        titleTextView.layout(
            paddingLeft,
            paddingTop
        )
        plusBtn.layout(
            paddingLeft,
            plusBtn.toViewVerticalCenter(titleTextView) + plusBtn.marginTop,
            true
        )
        dotIndicator.layout(
            paddingLeft,
            titleTextView.bottom + dotIndicator.marginTop
        )
    }


}