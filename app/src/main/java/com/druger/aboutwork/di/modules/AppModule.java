package com.druger.aboutwork.di.modules;

import android.content.Context;
import android.preference.PreferenceManager;

import com.druger.aboutwork.App;
import com.druger.aboutwork.db.RealmHelper;
import com.druger.aboutwork.utils.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by druger on 22.07.2017.
 */

@Module
public class AppModule {

    private App app;
    private PreferencesHelper preferencesHelper;
    private RealmHelper realmHelper;

    public AppModule(App app) {
        this.app = app;
        preferencesHelper = new PreferencesHelper(PreferenceManager.getDefaultSharedPreferences(app));
    }

    @Singleton
    @Provides
    Context provideAppContext() {
        return app;
    }

    @Singleton
    @Provides
    PreferencesHelper providePreferenceHelper() {
        return preferencesHelper;
    }

    @Singleton
    @Provides
    RealmHelper provideRealmHelper() {
        return realmHelper;
    }
}
