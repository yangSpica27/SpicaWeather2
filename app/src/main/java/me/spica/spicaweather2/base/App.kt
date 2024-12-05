package me.spica.spicaweather2.base

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

  companion object {
    @JvmStatic
    lateinit var instance: App
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
  }

  init {
    System.loadLibrary("liquidfun")
    System.loadLibrary("liquidfun_jni")
  }
}