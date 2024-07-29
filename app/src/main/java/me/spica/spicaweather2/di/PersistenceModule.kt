package me.spica.spicaweather2.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.spica.spicaweather2.persistence.AppDatabase
import javax.inject.Singleton

/**
 * 持久化模块
 */
@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {
    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase =
        Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                "spica_weather.db",
            ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCityDao(appDatabase: AppDatabase) = appDatabase.cityDao()

    @Provides
    @Singleton
    fun provideWeatherDao(appDatabase: AppDatabase) = appDatabase.weatherDao()
}
