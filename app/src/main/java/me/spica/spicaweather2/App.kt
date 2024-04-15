package me.spica.spicaweather2

import android.app.Application
import android.service.carrier.CarrierMessagingService.ResultCallback
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Authenticator
import okhttp3.Callback
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.internal.connection.RealCall
import timber.log.Timber
import java.io.IOException


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
