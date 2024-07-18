package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.getStatusBarHeight

class MainTitleLayout(context: Context, attributeSet: AttributeSet? = null) :
    AViewGroup(context, attributeSet) {

    val titleTextView = AppCompatTextView(context).apply {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setTextAppearance(R.style.TextAppearance_Material3_TitleLarge)
        text = "--"
        setTextColor(Color.WHITE)
    }

    val plusBtn = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        setText(R.string.add)
        setPadding(12.dp)
        setTextColor(Color.WHITE)
        textSize = 18f
        typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface.create(Typeface.DEFAULT, 500, false)
        } else {
            Typeface.DEFAULT
        }
    }

    val dotIndicator = WormDotsIndicator(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.topMargin = 12.dp
        }
        this.setPadding(0)
        this.setDotIndicatorColor(ContextCompat.getColor(context, R.color.white))
        this.setStrokeDotsIndicatorColor(ContextCompat.getColor(context, R.color.white))
    }

//    fun setBackgroundWhiteColor(progress: Float) {
//        setBackgroundColor(getColorWithAlpha(progress, Color.WHITE))
//        if (progress > 0.5) {
//            val up = ContextCompat.getDrawable(context, R.drawable.ic_plus)!!
//            val drawableUp = DrawableCompat.wrap(up)
//            DrawableCompat.setTint(
//                drawableUp,
//                ContextCompat.getColor(context, R.color.textColorPrimary)
//            )
//            dotIndicator.setDotIndicatorColor(
//                ContextCompat.getColor(
//                    context,
//                    R.color.textColorPrimary
//                )
//            )
//            dotIndicator.setStrokeDotsIndicatorColor(
//                ContextCompat.getColor(
//                    context,
//                    R.color.textColorPrimaryHint
//                )
//            )
//            titleTextView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
//        } else {
//            val up = ContextCompat.getDrawable(context, R.drawable.ic_plus)!!
//            val drawableUp = DrawableCompat.wrap(up)
//            DrawableCompat.setTint(drawableUp, ContextCompat.getColor(context, R.color.white))
//            plusBtn.setImageDrawable(drawableUp)
//            dotIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.white))
//            dotIndicator.setStrokeDotsIndicatorColor(ContextCompat.getColor(context, R.color.white))
//            titleTextView.setTextColor(Color.WHITE)
//        }
//    }

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
        plusBtn.autoMeasure()
        dotIndicator.autoMeasure()
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(
                titleTextView.measuredHeightWithMargins +
                        dotIndicator.measuredHeightWithMargins +
                        paddingBottom +
                        paddingTop, heightMeasureSpec
            )
        )
    }

    override fun onLayout(p0: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        titleTextView.layout(
            paddingLeft,
            paddingTop
        )
        plusBtn.layout(
            paddingRight,
            plusBtn.toViewVerticalCenter(titleTextView) + plusBtn.marginTop,
            true
        )
        dotIndicator.layout(
            paddingLeft,
            titleTextView.bottom + dotIndicator.marginTop
        )
    }
}
