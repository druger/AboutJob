package com.druger.aboutwork.di.modules

import android.content.Context
import android.preference.PreferenceManager
import com.druger.aboutwork.App
import com.druger.aboutwork.db.RealmHelper
import com.druger.aboutwork.utils.PreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by druger on 22.07.2017.
 */

@Module
class AppModule(private val app: App) {
    private val preferencesHelper: PreferencesHelper = PreferencesHelper(PreferenceManager.getDefaultSharedPreferences(app))
    private val realmHelper: RealmHelper = RealmHelper()

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
    internal fun provideRealmHelper(): RealmHelper {
        return realmHelper
    }
}