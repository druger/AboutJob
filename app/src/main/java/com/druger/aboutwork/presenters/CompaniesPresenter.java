package com.druger.aboutwork.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.CompanyResponse;
import com.druger.aboutwork.rest.ApiClient;
import com.druger.aboutwork.rest.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompaniesPresenter extends MvpPresenter<CompaniesView> {
    private static final String TAG = CompaniesPresenter.class.getSimpleName();

    private ApiService apiService;

    public void onCreate() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Get list companies from search on hh.ru
     */
    public void getCompanies(String query, int page) {

        Call<CompanyResponse> call = apiService.getCompanies(query, page);
        call.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                List<Company> companies = response.body().getItems();
                getViewState().showCompanies(companies);
                Log.d(TAG, "Companies size = " + companies.size());
            }

            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void getCompanyDetail(Company company) {
        Call<CompanyDetail> call = apiService.getCompanyDetail(company.getId());
        call.enqueue(new Callback<CompanyDetail>() {
            @Override
            public void onResponse(Call<CompanyDetail> call, Response<CompanyDetail> response) {
                CompanyDetail detail = response.body();
                getViewState().showCompanyDetail(detail);
            }

            @Override
            public void onFailure(Call<CompanyDetail> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
