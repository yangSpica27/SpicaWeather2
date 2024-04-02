package me.spica.spicaweather2.view.search_edit

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.widget.addTextChangedListener
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.view_group.AViewGroup

/**
 * 搜索框
 * Created by Spica on 2021/2/23.
 */
class SearchBarLayout(context: Context) : AViewGroup(context) {

    val editText = AppCompatEditText(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            24.dp
        )
        setPadding(12.dp, 4.dp, 12.dp, 4.dp)
        setBackgroundDrawable(null)
    }

    private val iconLeft = AppCompatImageView(context).apply {
        setImageResource(R.drawable.ic_search)
        layoutParams = LayoutParams(
            24.dp,
            24.dp,
        )
    }

    private val iconClear = AppCompatImageView(context).apply {
        setImageResource(R.drawable.ic_close)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        visibility = GONE
    }

    init {
        addView(iconLeft)
        addView(iconClear)
        addView(editText)
        setPadding(
            14.dp, 4.dp, 14.dp, 4.dp
        )
        editText.maxEms = 10
        editText.maxLines = 1
        setBackgroundResource(R.drawable.bg_edit_round)
        editText.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                iconClear.visibility = View.GONE
            } else {
                iconClear.visibility = View.VISIBLE
            }
        }
        iconClear.setOnClickListener {
            editText.text = null
        }
        editText.setHint("请输入您所在的城市名称")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        iconLeft.autoMeasure()
        iconClear.autoMeasure()
        editText.measure(
            (
                    measuredWidth -
                            iconLeft.measuredWidth -
                            iconClear.measuredWidth -
                            editText.marginLeft -
                            editText.marginRight
                    ).toExactlyMeasureSpec(),
            defaultHeightMeasureSpec(this)
        )
        setMeasuredDimension(
            (measuredWidth - marginLeft - marginRight),
            resolveSize(editText.measuredHeight + paddingTop + paddingBottom, heightMeasureSpec)
        )
    }

    override fun onLayout(change: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        iconLeft.layout(
            paddingLeft,
            0 + (height - iconLeft.measuredHeight) / 2
        )
        editText.layout(
            iconLeft.right + editText.marginLeft,
            paddingTop,
        )
        iconClear.layout(
            paddingRight,
            0 + (height - iconClear.height) / 2,
            true
        )
    }
}
