package me.spica.spicaweather2.di

import android.content.Context
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.spica.spicaweather2.network.model.HeClient
import me.spica.spicaweather2.network.model.HeService
import me.spica.spicaweather2.persistence.repository.HeRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 注入ohHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .retryOnConnectionFailure(true)
            .connectTimeout(5000L, TimeUnit.MILLISECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://devapi.qweather.com/v7/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideHeService(retrofit: Retrofit): HeService {

        return retrofit
            .create(HeService::class.java)
    }

    @Provides
    @Singleton
    fun provideHeClient(heService: HeService): HeClient {
        return HeClient(heService)
    }

    @Provides
    @Singleton
    fun provideHeRepository(heClient: HeClient): HeRepository {
        return HeRepository(heClient)
    }


}
