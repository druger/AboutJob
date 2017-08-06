package com.druger.aboutwork.di.modules;

import com.druger.aboutwork.rest.RestApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by druger on 22.07.2017.
 */

@Module
public class NetworkModule {

    private RestApi restApi;

    public NetworkModule() {
        restApi = new RestApi();
    }

    @Singleton
    @Provides
    RestApi provideApiClient() {
        return restApi;
    }
}
