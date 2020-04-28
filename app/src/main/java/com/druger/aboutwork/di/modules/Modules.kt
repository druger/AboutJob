package com.druger.aboutwork.di.modules

import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single { Analytics(FirebaseAnalytics.getInstance(androidApplication().applicationContext)) }
    single { RestApi() }
}