package com.druger.aboutwork.rest.endpoints

import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.rest.models.CompanyResponse

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by druger on 25.07.2016.
 */
interface CompanyEndPoint {
    @GET("/employers")
    fun getCompanies(@Query("text") query: String,
                     @Query("page") page: Int): Observable<CompanyResponse>

    @GET("/employers/{employer_id}")
    fun getCompanyDetail(@Path("employer_id") id: String): Observable<CompanyDetail>
}
