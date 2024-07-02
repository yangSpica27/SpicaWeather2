package me.spica.spicaweather2

import android.app.Activity
import android.app.Application
import android.view.WindowManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


/**
 * 柠檬天气v2.0
 * 佛祖保佑，永无bug
 * 代码不规范，维护两行泪
 */
@HiltAndroidApp
class App : Application() {

    companion object {
        @JvmStatic
        lateinit var instance: App
    }

    init {
        System.loadLibrary("liquidfun")
        System.loadLibrary("liquidfun_jni")
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        instance = this
    }

}
