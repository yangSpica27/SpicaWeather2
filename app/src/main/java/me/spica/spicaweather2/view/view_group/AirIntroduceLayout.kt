package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.getStatusBarHeight

class AirIntroduceLayout(
    context: Context,
) : AViewGroup(context) {
    private val title =
        AppCompatTextView(context).apply {
            text = context.getString(R.string.air_introduce_1)
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp + context.getStatusBarHeight()
                }
            gravity = Gravity.CENTER
            setTextAppearance(R.style.TextAppearance_Material3_HeadlineMedium)
        }

    private val subTitle =
        AppCompatTextView(context).apply {
            text = context.getString(R.string.air_introduce_2)
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 14.dp
                }
            setTextAppearance(R.style.TextAppearance_Material3_BodyMedium)
        }

    private val item1 =
        AirIntroduceItemLayout(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 20.dp
                }
            setData(
                context.getString(R.string.air_level1_introduce_name),
                context.getString(R.string.air_level1_introduce_title),
                context.getString(R.string.air_level1_introduce_body),
                ContextCompat.getColor(context, R.color.material_green_800),
                ContextCompat.getColor(context, R.color.material_green_100),
            )
        }
    private val item2 =
        AirIntroduceItemLayout(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
            setData(
                context.getString(R.string.air_level2_introduce_name),
                context.getString(R.string.air_level2_introduce_title),
                context.getString(R.string.air_level2_introduce_body),
                ContextCompat.getColor(context, R.color.material_light_green_800),
                ContextCompat.getColor(context, R.color.material_light_green_100),
            )
        }
    private val item3 =
        AirIntroduceItemLayout(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
            setData(
                context.getString(R.string.air_level3_introduce_name),
                context.getString(R.string.air_level3_introduce_title),
                context.getString(R.string.air_level3_introduce_body),
                ContextCompat.getColor(context, R.color.material_yellow_800),
                ContextCompat.getColor(context, R.color.material_yellow_100),
            )
        }
    private val item4 =
        AirIntroduceItemLayout(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
            setData(
                context.getString(R.string.air_level4_introduce_name),
                context.getString(R.string.air_level4_introduce_title),
                context.getString(R.string.air_level4_introduce_body),
                ContextCompat.getColor(context, R.color.material_orange_800),
                ContextCompat.getColor(context, R.color.material_orange_100),
            )
        }

    private val item5 =
        AirIntroduceItemLayout(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
            setData(
                context.getString(R.string.air_level5_introduce_name),
                context.getString(R.string.air_level5_introduce_title),
                context.getString(R.string.air_level5_introduce_body),
                ContextCompat.getColor(context, R.color.material_pink_800),
                ContextCompat.getColor(context, R.color.material_pink_100),
            )
        }

    private val item6 =
        AirIntroduceItemLayout(context).apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 12.dp
                }
            setData(
                context.getString(R.string.air_level6_introduce_name),
                context.getString(R.string.air_level6_introduce_title),
                context.getString(R.string.air_level6_introduce_body),
                ContextCompat.getColor(context, R.color.material_red_800),
                ContextCompat.getColor(context, R.color.material_red_100),
            )
        }

    init {
        setPadding(14.dp, 20.dp, 14.dp, 20.dp)
        addView(title)
        addView(subTitle)
        addView(item1)
        addView(item2)
        addView(item3)
        addView(item4)
        addView(item5)
        addView(item6)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        title.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            title.defaultHeightMeasureSpec(this),
        )
        subTitle.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            subTitle.defaultHeightMeasureSpec(this),
        )
        item1.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            item1.defaultHeightMeasureSpec(this),
        )
        item2.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            item2.defaultHeightMeasureSpec(this),
        )
        item3.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            item3.defaultHeightMeasureSpec(this),
        )
        item4.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            item4.defaultHeightMeasureSpec(this),
        )
        item5.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            item5.defaultHeightMeasureSpec(this),
        )
        item6.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            item6.defaultHeightMeasureSpec(this),
        )
        setMeasuredDimension(
            measuredWidth,
            paddingTop +
                paddingBottom +
                title.measuredHeightWithMargins +
                subTitle.measuredHeightWithMargins +
                item1.measuredHeightWithMargins +
                item2.measuredHeightWithMargins +
                item3.measuredHeightWithMargins +
                item4.measuredHeightWithMargins +
                item5.measuredHeightWithMargins +
                item6.measuredHeightWithMargins,
        )
    }

    override fun onLayout(
        p0: Boolean,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: Int,
    ) {
        title.layout(
            paddingLeft,
            paddingTop + title.marginTop,
        )
        subTitle.layout(
            paddingLeft,
            title.bottom + subTitle.marginTop,
        )
        item1.layout(
            paddingLeft,
            subTitle.bottom + item1.marginTop,
        )
        item2.layout(
            paddingLeft,
            item1.bottom + item2.marginTop,
        )
        item3.layout(
            paddingLeft,
            item2.bottom + item3.marginTop,
        )
        item4.layout(
            paddingLeft,
            item3.bottom + item4.marginTop,
        )
        item5.layout(
            paddingLeft,
            item4.bottom + item5.marginTop,
        )
        item6.layout(
            paddingLeft,
            item5.bottom + item6.marginTop,
        )
    }
}
