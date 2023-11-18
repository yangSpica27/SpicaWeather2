@file:Suppress("unused")

package me.spica.base.tools

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.BuildConfig
import me.spica.base.R
import timber.log.Timber

/**
 * 获取版本号
 */
fun Context.getVersion(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        "-1"
    }
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

fun doOnMainThreadIdle(action: () -> Unit, timeout: Long? = null) {
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
                if (BuildConfig.DEBUG) {
                    Timber.d("doOnMainThreadIdle: ${timeout}ms timeout!")
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



fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, textId, duration).show() }

fun Fragment?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { activity.toast(text, duration) }

fun Fragment?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) =
    this?.let { activity.toast(textId, duration) }

fun Context.getCompatColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)

// startActivityWithAnimation
inline fun <reified T : Activity> Context.startActivityWithAnimation(
    enterResId: Int = 0,
    exitResId: Int = 0
) {
    val intent = Intent(this, T::class.java)
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    ContextCompat.startActivity(this, intent, bundle)
}

inline fun <reified T : Activity> Context.startActivityWithAnimation(
    enterResId: Int = 0,
    exitResId: Int = 0,
    intentBody: Intent.() -> Unit
) {
    val intent = Intent(this, T::class.java)
    intent.intentBody()
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    ContextCompat.startActivity(this, intent, bundle)
}

fun View.getBitmap(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    draw(canvas)
    canvas.save()
    return bmp
}

val Int.dp: Float
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics
    )


fun View.show(anim:Boolean = true) {
    this.visibility = View.VISIBLE
    if (anim){
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_bottom))
    }
}


fun View.hide() {
    this.visibility = View.GONE
}

fun AppCompatActivity.addNewFragment(
    new_add_fragment: Fragment,
    container_view_id: Int,
    need_back_from_stack: Boolean = false
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
    hide_fragment_list:
    List<Fragment> = listOf()
) {

    val transaction = this.supportFragmentManager.beginTransaction()

    hide_fragment_list.forEach {
        transaction.hide(it) // 隐藏
    }
    transaction.show(show_old_fragment) // 显示

    transaction.commit() // 提交
}


fun Context.getStatusBarHeight(): Int {
    var height = 0
    val resourceId =
        applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        height = applicationContext.resources.getDimensionPixelSize(resourceId)
    }
    return height
}
