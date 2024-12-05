package me.spica.spicaweather2.di

import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.spica.spicaweather2.network.HeClient
import me.spica.spicaweather2.network.HeService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络模块
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  /**
   * 注入ohHttpClient
   */
  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient =
    OkHttpClient
      .Builder()
      .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
      .retryOnConnectionFailure(true)
      .connectTimeout(5000L, TimeUnit.MILLISECONDS)
      .build()

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit
      .Builder()
      .client(okHttpClient)
      .baseUrl("https://devapi.qweather.com/v7/")
      .addConverterFactory(MoshiConverterFactory.create())
      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
      .build()

  @Provides
  @Singleton
  fun provideHeService(retrofit: Retrofit): HeService =
    retrofit
      .create(HeService::class.java)

  @Provides
  @Singleton
  fun provideHeClient(heService: HeService): HeClient = HeClient(heService)

}
