package me.spica.spicaweather2.base

import android.R
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.color.MaterialColors


/**
 * Base activity for all activities in the app
 */
@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      navigationBarStyle = SystemBarStyle.light(
        Color.TRANSPARENT,
        Color.TRANSPARENT
      )
    )
  }


  private val FragmentActivity.windowInsetsCompat: WindowInsetsCompat?
    get() = ViewCompat.getRootWindowInsets(findViewById(R.id.content))

  // Get the height of the status bar
  protected fun FragmentActivity.getNavigationBarsHeight(): Int {
    val windowInsetsCompat = windowInsetsCompat ?: return 0
    return windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
  }


  protected fun FragmentActivity.getStatusBarsHeight(): Int {
    val windowInsetsCompat = windowInsetsCompat ?: return 0
    return windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
  }


  protected fun getColorSurface(): Int {
    return MaterialColors.getColor(
      this,
      com.google.android.material.R.attr.colorSurface,
      Color.BLACK
    )
  }


  protected fun getColorPrimary(): Int {
    return MaterialColors.getColor(
      this,
      com.google.android.material.R.attr.colorPrimary,
      Color.BLACK
    )
  }


  protected fun getColorPrimaryVariant(): Int {
    return MaterialColors.getColor(
      this,
      com.google.android.material.R.attr.colorPrimaryVariant,
      Color.BLACK
    )
  }

  protected fun getColorOnSurface(): Int {
    return MaterialColors.getColor(
      this,
      com.google.android.material.R.attr.colorOnSurface,
      Color.BLACK
    )
  }

}