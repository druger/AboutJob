package com.druger.aboutwork.rest.endpoints

import com.druger.aboutwork.rest.models.CityResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by druger on 01.04.2018.
 */

interface CitiesEndPoint {
    @GET("/suggests/areas")
    fun getCities(@Query("text") city: String): Single<CityResponse>
}
