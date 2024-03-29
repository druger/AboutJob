package com.druger.aboutwork

//import com.squareup.leakcanary.LeakCanary
//import com.squareup.leakcanary.RefWatcher
import android.app.Application
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


/**
 * Created by druger on 26.07.2016.
 */
@HiltAndroidApp
class App : Application() {

//    private lateinit var refWatcher: RefWatcher

    companion object {

//        fun getRefWatcher(context: Context): RefWatcher {
//            val app: App = context.applicationContext as App
//            return app.refWatcher
//        }
    }

    override fun onCreate() {
        super.onCreate()
//        setupLeakCanary()
        FirebaseApp.initializeApp(this)
        AndroidThreeTen.init(this)
        setupTimber()
        if (!BuildConfig.DEBUG) {
            setupAppCenter()
            setupAppMetrica()
        }
    }

    private fun setupAppMetrica() {
        YandexMetricaConfig
            .newConfigBuilder("246ea65a-18fd-4e97-b275-7c81f74e43b2")
            .build().apply {
                YandexMetrica.activate(applicationContext, this)
            }
        YandexMetrica.enableActivityAutoTracking(this)
    }

    private fun setupAppCenter() {
        AppCenter.start(this, "83d0f345-7236-45bf-a1cc-e5a774f37c39",
            Analytics::class.java, Crashes::class.java)
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupLeakCanary() {
//        if (BuildConfig.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                return
//            }
//            refWatcher = LeakCanary.install(this)
//        }
    }
}
