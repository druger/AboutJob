package com.druger.aboutwork;

import android.app.Application;
import android.content.Context;

import com.druger.aboutwork.di.components.AppComponent;
import com.druger.aboutwork.di.components.DaggerAppComponent;
import com.druger.aboutwork.di.modules.AppModule;
import com.druger.aboutwork.di.modules.NetworkModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by druger on 26.07.2016.
 */
public class App extends Application {

    private RefWatcher refWatcher;
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupLeakCanary();
        setupDagger2();
    }

    private void setupDagger2() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    private void setupLeakCanary() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            refWatcher = LeakCanary.install(this);
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        App app = (App) context.getApplicationContext();
        return app.refWatcher;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
