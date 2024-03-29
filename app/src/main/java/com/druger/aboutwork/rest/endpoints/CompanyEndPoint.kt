package com.druger.aboutwork.rest.endpoints

import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.rest.models.CompanyResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by druger on 25.07.2016.
 */
interface CompanyEndPoint {
    @GET("/employers")
    fun getCompanies(@Query("text") query: String,
                     @Query("page") page: Int,
                     @Query("only_with_vacancies") withVacancies: Boolean = true,
                     @Query("per_page") perPage: Int = 50
    ): Single<CompanyResponse>

    @GET("/employers/{employer_id}")
    fun getCompanyDetail(@Path("employer_id") id: String): Single<CompanyDetail>
}
