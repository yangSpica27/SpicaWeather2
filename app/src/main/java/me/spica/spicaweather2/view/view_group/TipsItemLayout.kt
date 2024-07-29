package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginTop
import me.spica.spicaweather2.R

// / 生活指数item布局
class TipsItemLayout(
    context: Context,
) : AViewGroup(context) {
    private val titleText =
        AppCompatTextView(context).apply {
            setTextAppearance(R.style.TextAppearance_Material3_TitleMedium)
        }

    private val descText =
        AppCompatTextView(context).apply {
            setTextAppearance(R.style.TextAppearance_Material3_BodyMedium)
            layoutParams =
                MarginLayoutParams(
                    MarginLayoutParams.WRAP_CONTENT,
                    MarginLayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 8.dp
                }
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
        setBackgroundResource(R.drawable.bg_card)
        backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1a4a4a4a"))
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
