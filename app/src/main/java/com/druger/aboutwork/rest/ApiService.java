package com.druger.aboutwork.rest;

import com.druger.aboutwork.model.CatalogCompanies;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by druger on 25.07.2016.
 */
public interface ApiService {
    @GET("/industries")
    Call<ArrayList<CatalogCompanies>> getCatalogCompanies();
}
