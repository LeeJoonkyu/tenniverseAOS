package com.heejae.tenniverse.di

import android.content.Context
import com.heejae.tenniverse.data.PreferenceUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun providePreferenceUtil(@ApplicationContext context: Context): PreferenceUtil =
        PreferenceUtil(context)
}