package me.spica.weather.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import me.spica.spicaweather2.R
import me.spica.spicaweather2.tools.dp

class RainImageView : LottieAnimationView {

    // 降雨概率
    private var rainfallProbability = 60f
        set(value) {
            field = value
            text = "${rainfallProbability}mm"
            postInvalidate()
        }

    // 文本 下雨概率
    private var text = ""

    // 文本的bound
    private val textBound = Rect()

    // 文本画笔
    private val textPaint = TextPaint( ).apply {
        color = ContextCompat.getColor(context, R.color.water_color)
        textSize = 10.dp
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        if (rainfallProbability > 0F) {
            textPaint.getTextBounds(text, 0, text.length, textBound)
            //
            canvas.drawText(
                text,
                width - textBound.width() * 1f,
                textBound.height() * 1f + height / 3f, textPaint
            )
        }
    }
}
