package me.spica.spicaweather2.view.weather_detail_card

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

abstract class CardLinearlayout : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


}
