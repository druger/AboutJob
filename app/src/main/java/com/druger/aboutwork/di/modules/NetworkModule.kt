package com.druger.aboutwork.di.modules

import com.druger.aboutwork.rest.RestApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by druger on 22.07.2017.
 */

@Module
class NetworkModule {

    private val restApi: RestApi = RestApi()

    @Singleton
    @Provides
    internal fun provideApiClient(): RestApi {
        return restApi
    }
}
