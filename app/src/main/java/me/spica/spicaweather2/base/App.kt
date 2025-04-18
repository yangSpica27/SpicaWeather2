package me.spica.spicaweather2.base

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import leakcanary.LeakCanary
import me.spica.spicaweather2.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

  companion object {
    @JvmStatic
    lateinit var instance: App
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    if (BuildConfig.DEBUG){
      Timber.plant(Timber.DebugTree())
    }
  }

  init {
    System.loadLibrary("liquidfun")
    System.loadLibrary("liquidfun_jni")
  }
}