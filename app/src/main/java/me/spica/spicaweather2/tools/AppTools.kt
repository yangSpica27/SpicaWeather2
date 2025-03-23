@file:Suppress("unused")

package me.spica.spicaweather2.tools

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import android.util.DisplayMetrics
import android.view.Display
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.ViewGroupUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * 获取版本号
 */
fun Context.getVersion(): String = try {
  val packageInfo = packageManager.getPackageInfo(packageName, 0)
  packageInfo.versionName ?: "-1"
} catch (e: Exception) {
  e.printStackTrace()
  "-1"
}

// 隐藏软键盘
fun View.hideKeyboard() {
  val inputMethodManager =
    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

// 显示软键盘
fun View.showKeyboard() {
  if (requestFocus()) {
    val inputMethodManager =
      context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
  }
}

@ExperimentalContracts
fun doOnMainThreadIdle(
  action: () -> Unit,
  timeout: Long? = null,
) {
  // 使用 kotlin 契约来告知编译器 action 函数体中的代码是否会且只会执行一次
  // 调用方在调用该方法后，必须传入一个 action 函数体，并在内部执行。
  // InvocationKind.AT_MOST_ONCE 的意思是该函数体至多只会执行一次。
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  val handler = Handler(Looper.getMainLooper())

  val idleHandler = MessageQueue.IdleHandler {
    handler.removeCallbacksAndMessages(null)
    try {
      action()
    } catch (_: Exception) {
    }

    return@IdleHandler false
  }
  fun setupIdleHandler(queue: MessageQueue) {
    if (timeout != null) {
      handler.postDelayed({
        queue.removeIdleHandler(idleHandler)
        try {
          action()
        } catch (_: Exception) {
        }
      }, timeout)
    }
    queue.addIdleHandler(idleHandler)
  }
  if (Looper.getMainLooper() == Looper.myLooper()) {
    setupIdleHandler(Looper.myQueue())
  } else {
    setupIdleHandler(Looper.getMainLooper().queue)
  }
}

fun getRefreshRate(context: Context): Float = try {
  val displayManager =
    context.getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager
  val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
  val displayMode = display?.mode
  displayMode?.refreshRate ?: 60f
} catch (e: Exception) {
  60f
}

// 扩大剑姬区域
@SuppressLint("RestrictedApi")
fun View.expand(
  dx: Int,
  dy: Int,
) {
  // 将刚才定义代理类放到方法内部，调用方不需要了解这些细节
  class MultiTouchDelegate(
    bound: Rect? = null,
    delegateView: View,
  ) : TouchDelegate(bound, delegateView) {
    val delegateViewMap = mutableMapOf<View, Rect>()
    private var delegateView: View? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
      val x = event.x.toInt()
      val y = event.y.toInt()
      var handled = false
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          delegateView = findDelegateViewUnder(x, y)
        }

        MotionEvent.ACTION_CANCEL -> {
          delegateView = null
        }
      }
      delegateView?.let {
        event.setLocation(it.width / 2f, it.height / 2f)
        handled = it.dispatchTouchEvent(event)
      }
      return handled
    }

    private fun findDelegateViewUnder(
      x: Int,
      y: Int,
    ): View? {
      delegateViewMap.forEach { entry -> if (entry.value.contains(x, y)) return entry.key }
      return null
    }
  }

  // 获取当前控件的父控件
  val parentView = parent as? ViewGroup
  // 若父控件不是 ViewGroup, 则直接返回
  parentView ?: return

  // 若父控件未设置触摸代理，则构建 MultiTouchDelegate 并设置给它
  if (parentView.touchDelegate == null) {
    parentView.touchDelegate = MultiTouchDelegate(delegateView = this)
  }
  post {
    val rect = Rect()
    // 获取子控件在父控件中的区域
    ViewGroupUtils.getDescendantRect(parentView, this, rect)
    // 将响应区域扩大
    rect.inset(-dx, -dy)
    // 将子控件作为代理控件添加到 MultiTouchDelegate 中
    (parentView.touchDelegate as? MultiTouchDelegate)?.delegateViewMap?.put(this, rect)
  }
}

fun Context?.toast(
  text: CharSequence,
  duration: Int = Toast.LENGTH_LONG,
) = this?.let { Toast.makeText(it, text, duration).show() }

fun Context?.toast(
  @StringRes textId: Int,
  duration: Int = Toast.LENGTH_LONG,
) = this?.let { Toast.makeText(it, textId, duration).show() }

fun Fragment?.toast(
  text: CharSequence,
  duration: Int = Toast.LENGTH_LONG,
) = this?.let { activity.toast(text, duration) }

fun Fragment?.toast(
  @StringRes textId: Int,
  duration: Int = Toast.LENGTH_LONG,
) = this?.let { activity.toast(textId, duration) }

fun Context.getCompatColor(
  @ColorRes id: Int,
) = ContextCompat.getColor(this, id)

fun Context.getCompatDrawable(
  @DrawableRes id: Int,
) = ContextCompat.getDrawable(this, id)

fun Context.getInteger(
  @IntegerRes id: Int,
) = resources.getInteger(id)

fun Context.getBoolean(
  @BoolRes id: Int,
) = resources.getBoolean(id)

// startActivityWithAnimation
inline fun <reified T : Activity> Context.startActivityWithAnimation(
  enterResId: Int = 0,
  exitResId: Int = 0,
) {
  val intent = Intent(this, T::class.java)
  val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
  ContextCompat.startActivity(this, intent, bundle)
}

inline fun <reified T : Activity> Context.startActivityWithAnimation(
  enterResId: Int = 0,
  exitResId: Int = 0,
  intentBody: Intent.() -> Unit,
) {
  val intent = Intent(this, T::class.java)
  intent.intentBody()
  val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
  ContextCompat.startActivity(this, intent, bundle)
}

// fun View.getBitmap(): Bitmap {
//    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(bmp)
//    draw(canvas)
//    canvas.save()
//    return bmp
// }

val Int.dp: Float
  get() = android.util.TypedValue.applyDimension(
    android.util.TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics,
  )

fun View.show() {
  this.visibility = View.VISIBLE
}

fun View.hide() {
  this.visibility = View.GONE
}

fun AppCompatActivity.addNewFragment(
  new_add_fragment: Fragment,
  container_view_id: Int,
  need_back_from_stack: Boolean = false,
) {
  val transaction = this.supportFragmentManager.beginTransaction()

  transaction.add(container_view_id, new_add_fragment) // 新加
  transaction.show(new_add_fragment) // 显示

  if (need_back_from_stack) {
    transaction.addToBackStack(null) // 加入回退栈。
    // TODO 并且，如果按照默认模式来的话，返回键，就会从栈顶一直清空到栈底的。（默认的【返回操作】）。（当然，你也可以自己在onBackPress里面玩些花样）
  }
  transaction.commit() // 最终提交。
}

fun AppCompatActivity.showOldFragment(
  show_old_fragment: Fragment,
  hide_fragment_list: List<Fragment> = listOf(),
) {
  val transaction = this.supportFragmentManager.beginTransaction()

  hide_fragment_list.forEach {
    transaction.hide(it) // 隐藏
  }
  transaction.show(show_old_fragment) // 显示

  transaction.commit() // 提交
}

@SuppressLint("InternalInsetResource")
fun Context.getStatusBarHeight(): Int {
  var height = 0
  val resourceId =
    applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    height = applicationContext.resources.getDimensionPixelSize(resourceId)
  }
  return height
}

private var screenSize: Point? = null

@Suppress("DEPRECATION")
private fun initScreenSize(context: Context) {
  if (screenSize == null) {
    screenSize = Point()
    val outMetrics = DisplayMetrics()
    ContextCompat.getDisplayOrDefault(context).getRealMetrics(outMetrics)
    screenSize?.x = outMetrics.widthPixels
    screenSize?.y = outMetrics.heightPixels
  }
}

fun Context.getScreenWidth(): Int {
  initScreenSize(this)
  return screenSize?.x ?: 0
}

fun Context.getScreenHeight(): Int {
  initScreenSize(this)
  return screenSize?.y ?: 0
}

internal fun View.locationOnScreen(
  intBuffer: IntArray = IntArray(2),
  rectBuffer: Rect = Rect(),
  ignoreTranslations: Boolean = false,
): Rect {
  getLocationOnScreen(intBuffer)
  if (ignoreTranslations) {
    intBuffer[0] -= translationX.toInt()
    intBuffer[1] -= translationY.toInt()
  }
  rectBuffer.set(intBuffer[0], intBuffer[1], intBuffer[0] + width, intBuffer[1] + height)
  return rectBuffer
}
