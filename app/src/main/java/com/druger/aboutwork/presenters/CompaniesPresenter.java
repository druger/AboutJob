package com.druger.aboutwork.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.db.RealmHelper;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.realm.CompanyRealm;
import com.druger.aboutwork.rest.RestApi;
import com.druger.aboutwork.rest.models.CompanyResponse;
import com.druger.aboutwork.utils.rx.RxUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

import static io.realm.OrderedCollectionChangeSet.State.INITIAL;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompaniesPresenter extends BasePresenter<CompaniesView> {

    private RealmResults<CompanyRealm> companies;
    private List<Company> companiesWithCity;
    private Company company;

    private OrderedRealmCollectionChangeListener<RealmResults<CompanyRealm>> realmCallback =
            (companies, changeSet) -> {
                if (companies.size() > 0 && changeSet.getState() == INITIAL) {
                    getViewState().showWatchedRecently();
                    getViewState().showCompaniesRealm();
                }
            };

    @Inject
    public CompaniesPresenter(RestApi restApi, RealmHelper realmHelper) {
        this.restApi = restApi;
        this.realmHelper = realmHelper;
        companiesWithCity = new ArrayList<>();
    }

    public void getCompanies(String query, int page) {
        requestGetCompanies(query, page);
        companiesWithCity.clear();
    }

    private void requestGetCompanies(String query, int page) {
        Disposable request = restApi.company.getCompanies(query, page)
                .flatMapIterable(CompanyResponse::getItems)
                .flatMap(company1 -> {
                    company = company1;
                    return restApi.company.getCompanyDetail(company1.getId());
                })
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanies, this::handleError, this::showCompanies);

        unSubscribeOnDestroy(request);
    }

    private void successGetCompanies(CompanyDetail companyDetail) {
        company.setCity(companyDetail.getArea().getName());
        companiesWithCity.add(company);
    }

    @Override
    protected void handleError(Throwable throwable) {
        super.handleError(throwable);
        getViewState().showProgress(false);
    }

    private void showCompanies() {
        getViewState().showCompanies(companiesWithCity);
    }

    public void saveCompanyToDb(CompanyRealm company) {
        realmHelper.saveCompany(company);
    }

    public RealmResults<CompanyRealm> getCompaniesFromDb() {
        companies = realmHelper.getCompanies();
        companies.addChangeListener(realmCallback);
        return companies;
    }

    public void removeRealmListener() {
        companies.removeChangeListener(realmCallback);
    }
}
