package me.spica.spicaweather2.view.view_group

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import me.spica.spicaweather2.R

class AirIntroduceItemLayout(context: Context) : AViewGroup(context) {


    private val leftTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setTextAppearance(R.style.TextAppearance_Material3_TitleMedium)
        setTextColor(Color.WHITE)
        text = "优秀\n0-50"
    }

    private val rightText1View = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setTextAppearance(R.style.TextAppearance_Material3_BodyLarge)
        setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
        text = "空气质量令人满意，基本无空气污染"
    }

    private val rightText2View = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 14.dp
        }
        setTextAppearance(R.style.TextAppearance_Material3_BodyLarge)
        setTextColor(ContextCompat.getColor(context, R.color.textColorPrimaryLight))
        text = "儿童、老年人及心脏病、呼吸系统疾病患者应避免长时间、高强度的户外锻炼，一般人群适量减少户外运动"
    }

    private var leftColor = ContextCompat.getColor(context, R.color.light_blue_600)

    private var rightColor = ContextCompat.getColor(context, R.color.light_blue_200)


    fun setData(leftText: String, rightText1: String, rightText2: String, leftColor: Int, rightColor: Int) {
        leftTextView.text = leftText
        rightText1View.text = rightText1
        rightText2View.text = rightText2
        this.leftColor = leftColor
        this.rightColor = rightColor
    }


    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(14.dp, 12.dp, 14.dp, 12.dp)
        addView(leftTextView)
        addView(rightText1View)
        addView(rightText2View)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cliPath.reset()
        cliPath.addRoundRect(
            0f, 0f, w.toFloat(),
            h.toFloat(),
            floatArrayOf(8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat()),
            Path.Direction.CW
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        leftTextView.autoMeasure()
        rightText1View.measure(
            (.7f * measuredWidth - paddingLeft - paddingRight).toInt().toExactlyMeasureSpec(),
            rightText1View.defaultHeightMeasureSpec(this),
        )
        rightText2View.measure(
            (.7f * measuredWidth - paddingLeft - paddingRight).toInt().toExactlyMeasureSpec(),
            rightText2View.defaultHeightMeasureSpec(this),
        )
        setMeasuredDimension(
            measuredWidth,
            paddingTop + paddingBottom +
                rightText1View.measuredHeightWithMargins +
                rightText2View.measuredHeightWithMargins
        )
    }


    private val cliPath = Path()

    private val backGroundPaint = Paint().apply {
        color = Color.WHITE
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(cliPath)
        backGroundPaint.color = leftColor
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), backGroundPaint)
        backGroundPaint.color = rightColor
        canvas.drawRect(width * 1f, 0f, .3f * measuredWidth, measuredHeight.toFloat(), backGroundPaint)
        super.dispatchDraw(canvas)
    }


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        leftTextView.layout(
            paddingLeft,
            leftTextView.toVerticalCenter(this)
        )
        rightText1View.layout(
            (.3f * measuredWidth + paddingLeft).toInt(),
            paddingTop
        )
        rightText2View.layout(
            rightText1View.left,
            rightText1View.bottom + rightText2View.marginTop
        )
    }

}