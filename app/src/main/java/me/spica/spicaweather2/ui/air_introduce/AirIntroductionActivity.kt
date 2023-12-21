package me.spica.spicaweather2.ui.air_introduce

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import me.spica.spicaweather2.R
import me.spica.spicaweather2.view.view_group.AirIntroduceLayout
import rikka.material.app.MaterialActivity

class AirIntroductionActivity : MaterialActivity() {

    private val layout  by lazy {
        AirIntroduceLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(layout)
            overScrollMode = ScrollView.OVER_SCROLL_NEVER
            setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }
        setContentView(scrollView)
        WindowInsetsControllerCompat(window, scrollView).isAppearanceLightStatusBars = true
    }


}