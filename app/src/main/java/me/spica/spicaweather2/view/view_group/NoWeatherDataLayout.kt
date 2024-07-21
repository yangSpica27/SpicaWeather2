package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import me.spica.spicaweather2.R

class NoWeatherDataLayout(context: Context) : AViewGroup(context) {

//    private val loadingFailImageView = AppCompatImageView(context).apply {
//        setImageResource(R.drawable.img_loading_fail)
//        background = ContextCompat.getDrawable(context, R.drawable.bg_card)
//        updatePadding(
//            20.dp,
//            20.dp,
//            20.dp,
//            20.dp
//        )
//    }

    val reloadBtn = MaterialTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        text = "暂无数据，点击重试"
        gravity = Gravity.CENTER
        textAlignment = MaterialButton.TEXT_ALIGNMENT_CENTER
        setTextColor(ContextCompat.getColor(context, R.color.white))
        textSize = 20f
        updatePadding(bottom = 120.dp)
    }

    init {
        setPadding(22.dp, 0, 22.dp, 0)
    }

    init {
        addView(reloadBtn)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(ContextCompat.getColor(context, R.color.material_grey_500))
    }

    override fun onLayout(changed: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {


        reloadBtn.layout(
            paddingLeft,
            paddingTop,
            false
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
       measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(measuredHeight, heightMeasureSpec)
        )
    }
}
