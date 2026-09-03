package com.jenil.f1comp.di

import com.jenil.f1comp.data.remote.ChatApiService
import com.jenil.f1comp.data.remote.F1ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://f1-companion-api-ba5k.onrender.com/"
    private const val CHAT_URL = "http://10.0.2.2:8001/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("F1Retrofit")
    fun provideF1Retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideF1ApiService(@Named("F1Retrofit") retrofit: Retrofit): F1ApiService {
        return retrofit.create(F1ApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("ChatRetrofit")
    fun provideChatRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CHAT_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideChatApiService(@Named("ChatRetrofit") retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }
}