package com.druger.aboutwork

import android.app.Application
import android.content.Context
import com.druger.aboutwork.db.RealmHelper.Companion.REALM_NAME
import com.druger.aboutwork.db.RealmHelper.Companion.SCHEMA_VERSION

import com.druger.aboutwork.di.components.AppComponent
import com.druger.aboutwork.di.components.DaggerAppComponent
import com.druger.aboutwork.di.modules.AppModule
import com.druger.aboutwork.di.modules.NetworkModule
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

import io.realm.Realm
import io.realm.RealmConfiguration


/**
 * Created by druger on 26.07.2016.
 */
class App : Application() {

    private lateinit var refWatcher: RefWatcher

    companion object {
        lateinit var appComponent: AppComponent
            private set

        fun getRefWatcher(context: Context): RefWatcher {
            val app: App = context.applicationContext as App
            return app.refWatcher
        }
    }

    override fun onCreate() {
        super.onCreate()
        setupLeakCanary()
        setupDagger2()
        setupRealm()
    }

    private fun setupDagger2() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule())
                .build()
    }

    private fun setupLeakCanary() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return
            }
            refWatcher = LeakCanary.install(this)
        }
    }

    private fun setupRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION)
                .name(REALM_NAME)
                .build()
        Realm.setDefaultConfiguration(config)
    }
}
