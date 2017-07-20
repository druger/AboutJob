package com.druger.aboutwork.rest;

import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.CompanyResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by druger on 25.07.2016.
 */
public interface ApiService {
    @GET("/employers")
    Observable<CompanyResponse> getCompanies(@Query("text") String query,
                                                      @Query("page") int page);

    @GET("/employers/{employer_id}")
    Observable<CompanyDetail> getCompanyDetail(@Path("employer_id") String id);
}
