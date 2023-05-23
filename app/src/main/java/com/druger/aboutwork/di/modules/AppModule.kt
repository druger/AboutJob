package com.druger.aboutwork.di.modules

import android.content.Context
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideAnalytics(@ApplicationContext context: Context) =
        Analytics(FirebaseAnalytics.getInstance(context))

    @Singleton
    @Provides
    fun provideRestApi() = RestApi()
}