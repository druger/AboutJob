package com.druger.aboutwork.rest;

import com.druger.aboutwork.rest.endpoints.CitiesEndPoint;
import com.druger.aboutwork.rest.endpoints.CompanyEndPoint;
import com.druger.aboutwork.rest.endpoints.VacancyEndpoint;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by druger on 25.07.2016.
 */
public class RestApi {

    public static final String BASE_URL = "https://api.hh.ru/";

    public final CompanyEndPoint company;
    public final CitiesEndPoint cities;
    public final VacancyEndpoint vacancies;

    public RestApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        company = retrofit.create(CompanyEndPoint.class);
        cities = retrofit.create(CitiesEndPoint.class);
        vacancies = retrofit.create(VacancyEndpoint.class);
    }
}
