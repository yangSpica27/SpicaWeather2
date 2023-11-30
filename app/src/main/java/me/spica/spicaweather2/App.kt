package me.spica.spicaweather2

import android.app.Application
import com.badlogic.gdx.Gdx
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class App :Application(){

    companion object{
        lateinit var instance:App
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        instance = this

    }
}