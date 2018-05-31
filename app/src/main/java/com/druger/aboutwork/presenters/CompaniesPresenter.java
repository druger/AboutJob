package com.druger.aboutwork.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.db.RealmHelper;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.rest.RestApi;
import com.druger.aboutwork.rest.models.CompanyResponse;
import com.druger.aboutwork.utils.rx.RxUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompaniesPresenter extends BasePresenter<CompaniesView> {

    private RealmResults<Company> companies;

    private OrderedRealmCollectionChangeListener<RealmResults<Company>> realmCallback =
            (companies, changeSet) -> {
                getViewState().showWatchedRecently();
                getViewState().showCompanies(companies);
            };

    @Inject
    public CompaniesPresenter(RestApi restApi, RealmHelper realmHelper) {
        this.restApi = restApi;
        this.realmHelper = realmHelper;
    }

    public void getCompanies(String query, int page) {
        getViewState().showProgress(true);
        requestGetCompanies(query, page);
    }

    private void requestGetCompanies(String query, int page) {
        Disposable request = restApi.company.getCompanies(query, page)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanies, this::handleError);

        unSubscribeOnDestroy(request);
    }

    @Override
    protected void handleError(Throwable throwable) {
        super.handleError(throwable);
        getViewState().showProgress(false);
    }

    private void successGetCompanies(CompanyResponse response) {
        getViewState().showProgress(false);
        List<com.druger.aboutwork.model.Company> companies = response.getItems();
        getViewState().showCompanies(companies);
        Log.d(TAG, "Companies size = " + companies.size());
    }

    public void saveCompanyToDb(Company company) {
        realmHelper.saveCompany(company);
    }

    public RealmResults<Company> getCompaniesFromDb() {
        companies = realmHelper.getCompanies();
        companies.addChangeListener(realmCallback);
        return companies;
    }

    public void removeRealmListener() {
        companies.removeChangeListener(realmCallback);
    }
}
