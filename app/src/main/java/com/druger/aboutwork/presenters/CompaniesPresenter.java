package com.druger.aboutwork.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.CompanyResponse;
import com.druger.aboutwork.rest.ApiClient;
import com.druger.aboutwork.rest.ApiService;
import com.druger.aboutwork.utils.rx.RxUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompaniesPresenter extends BasePresenter<CompaniesView> {
    private static final String TAG = CompaniesPresenter.class.getSimpleName();

    private ApiService apiService;

    public void onCreate() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Get list companies from search on hh.ru
     */
    public void getCompanies(String query, int page) {
        Disposable request = apiService.getCompanies(query, page)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanies, this::handleError);

        unSubscribeOnDestroy(request);
    }

    private void handleError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private void successGetCompanies(CompanyResponse response) {
        List<Company> companies = response.getItems();
        getViewState().showCompanies(companies);
        Log.d(TAG, "Companies size = " + companies.size());
    }

    public void getCompanyDetail(Company company) {
        Disposable request = apiService.getCompanyDetail(company.getId())
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanyDetails, this::handleError);

        unSubscribeOnDestroy(request);
    }

    private void successGetCompanyDetails(CompanyDetail companyDetail) {
        getViewState().showCompanyDetail(companyDetail);
    }
}
