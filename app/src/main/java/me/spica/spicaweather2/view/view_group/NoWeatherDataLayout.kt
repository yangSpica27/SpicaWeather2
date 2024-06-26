package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import com.google.android.material.button.MaterialButton
import me.spica.spicaweather2.R

class NoWeatherDataLayout(context: Context) : AViewGroup(context) {

    private val loadingFailImageView = AppCompatImageView(context).apply {
        setImageResource(R.drawable.img_loading_fail)
        background = ContextCompat.getDrawable(context, R.drawable.bg_card)
        updatePadding(
            20.dp,
            20.dp,
            20.dp,
            20.dp
        )
    }

    val reloadBtn = MaterialButton(context).apply {
        text = "重试"
    }

    init {
        setPadding(22.dp, 0, 22.dp, 0)
    }

    init {
        addView(loadingFailImageView)
        addView(reloadBtn)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onLayout(changed: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {

        loadingFailImageView.layout(
            paddingLeft,
            measuredHeight / 2 - loadingFailImageView.height / 2 - reloadBtn.height
        )

        reloadBtn.layout(
            paddingLeft,
            loadingFailImageView.bottom + 20.dp
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        loadingFailImageView.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            loadingFailImageView.defaultHeightMeasureSpec(this)
        )
        reloadBtn.measure(
            (measuredWidth - paddingLeft - paddingRight).toExactlyMeasureSpec(),
            reloadBtn.defaultHeightMeasureSpec(this)
        )
        setMeasuredDimension(
            resolveSize(measuredWidth, widthMeasureSpec),
            resolveSize(measuredHeight, heightMeasureSpec)
        )
    }
}
