package me.spica.spicaweather2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class App :Application(){

    companion object{
        @JvmStatic
        lateinit var instance:App
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        instance = this
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");
    }
}