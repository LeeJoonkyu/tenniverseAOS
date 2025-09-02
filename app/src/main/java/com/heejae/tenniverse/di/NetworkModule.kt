package com.heejae.tenniverse.di

import com.heejae.tenniverse.data.service.FCMApiService
import com.heejae.tenniverse.util.BASE_URL
import com.heejae.tenniverse.util.HEADER_KEY_AUTHORIZATION
import com.heejae.tenniverse.util.HEADER_KEY_CONTENT_TYPE
import com.heejae.tenniverse.util.HEADER_VALUE_AUTHORIZATION
import com.heejae.tenniverse.util.HEADER_VALUE_CONTENT_TYPE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOfficialRetrofit2(): FCMApiService {
        return Retrofit.Builder()
            .client(provideOfficialOKHttp())
            .baseUrl(BASE_URL)
            .addConverterFactory(provideConverterFactory())
            .build()
            .create(FCMApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOfficialOKHttp(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            retryOnConnectionFailure(true)
            addInterceptor(getLoggingInterceptor())
            addInterceptor(AppInterceptor())
        }.build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader(HEADER_KEY_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE)
                .addHeader(HEADER_KEY_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION)
                .build()
            proceed(newRequest)
        }
    }
}