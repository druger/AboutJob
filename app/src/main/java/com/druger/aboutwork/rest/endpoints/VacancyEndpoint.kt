package com.druger.aboutwork.rest.endpoints

import com.druger.aboutwork.rest.models.VacancyResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface VacancyEndpoint {
    @GET("/suggests/vacancy_search_keyword")
    fun getVacancies(@Query("text") position: String): Observable<VacancyResponse>
}