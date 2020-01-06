package com.druger.aboutwork.di.modules

import android.content.Context
import android.preference.PreferenceManager
import com.druger.aboutwork.App
import com.druger.aboutwork.utils.Analytics
import com.druger.aboutwork.utils.PreferencesHelper
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by druger on 22.07.2017.
 */

@Module
class AppModule(private val app: App) {
    private val preferencesHelper: PreferencesHelper = PreferencesHelper(PreferenceManager.getDefaultSharedPreferences(app))
    private val analytics: Analytics = Analytics(FirebaseAnalytics.getInstance(app))

    @Singleton
    @Provides
    internal fun provideAppContext(): Context {
        return app
    }

    @Singleton
    @Provides
    internal fun providePreferenceHelper(): PreferencesHelper {
        return preferencesHelper
    }

    @Singleton
    @Provides
    internal fun provideAnalytics(): Analytics = analytics
}
