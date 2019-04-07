package com.druger.aboutwork.rest

import com.druger.aboutwork.rest.endpoints.CitiesEndPoint
import com.druger.aboutwork.rest.endpoints.CompanyEndPoint
import com.druger.aboutwork.rest.endpoints.VacancyEndpoint
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by druger on 25.07.2016.
 */
class RestApi {

    val company: CompanyEndPoint
    val cities: CitiesEndPoint
    val vacancies: VacancyEndpoint

    init {
        val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        company = retrofit.create(CompanyEndPoint::class.java)
        cities = retrofit.create(CitiesEndPoint::class.java)
        vacancies = retrofit.create(VacancyEndpoint::class.java)
    }

    companion object {

        const val BASE_URL = "https://api.hh.ru/"
    }
}
