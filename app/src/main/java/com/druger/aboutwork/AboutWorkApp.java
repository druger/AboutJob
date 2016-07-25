package com.druger.aboutwork;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by druger on 26.07.2016.
 */
public class AboutWorkApp extends Application {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        AboutWorkApp app = (AboutWorkApp) context.getApplicationContext();
        return app.refWatcher;
    }
}
