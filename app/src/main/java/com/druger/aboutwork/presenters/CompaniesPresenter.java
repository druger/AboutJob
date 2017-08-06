package com.druger.aboutwork.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.CompanyResponse;
import com.druger.aboutwork.rest.RestApi;
import com.druger.aboutwork.utils.rx.RxUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompaniesPresenter extends BasePresenter<CompaniesView> {

    @Inject
    public CompaniesPresenter(RestApi restApi) {
        this.restApi = restApi;
    }

    /**
     * Get list companies from search on hh.ru
     */
    public void getCompanies(String query, int page) {
        Disposable request = restApi.company.getCompanies(query, page)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanies, this::handleError);

        unSubscribeOnDestroy(request);
    }

    protected void handleError(Throwable throwable) {

    }

    private void successGetCompanies(CompanyResponse response) {
        List<com.druger.aboutwork.model.Company> companies = response.getItems();
        getViewState().showCompanies(companies);
        Log.d(TAG, "Companies size = " + companies.size());
    }

    public void getCompanyDetail(com.druger.aboutwork.model.Company company) {
        Disposable request = restApi.company.getCompanyDetail(company.getId())
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanyDetails, this::handleError);

        unSubscribeOnDestroy(request);
    }

    private void successGetCompanyDetails(CompanyDetail companyDetail) {
        getViewState().showCompanyDetail(companyDetail);
    }
}
