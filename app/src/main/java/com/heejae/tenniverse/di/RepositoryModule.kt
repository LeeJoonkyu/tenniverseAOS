package com.heejae.tenniverse.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.MasterRepository
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.data.service.FCMApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth): UserRepository =
        UserRepository(auth)

    @Provides
    @Singleton
    fun provideMasterRepository(): MasterRepository = MasterRepository()

    @Provides
    @Singleton
    fun provideRentRepository(
        fcmService: FCMApiService,
        @ApplicationContext context: Context,
    ): RentRepository = RentRepository(fcmService, context)


}