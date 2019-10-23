package com.druger.aboutwork.presenters;

import com.druger.aboutwork.db.RealmHelper;
import com.druger.aboutwork.interfaces.view.CompaniesView;
import com.druger.aboutwork.model.realm.CompanyRealm;
import com.druger.aboutwork.rest.RestApi;
import com.druger.aboutwork.rest.models.CompanyResponse;
import com.druger.aboutwork.utils.rx.RxUtils;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import moxy.InjectViewState;

import static io.realm.OrderedCollectionChangeSet.State.INITIAL;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompaniesPresenter extends BasePresenter<CompaniesView> {

    private RealmResults<CompanyRealm> companies;

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
    }

    public void getCompanies(String query, int page) {
        requestGetCompanies(query, page);
    }

    private void requestGetCompanies(String query, int page) {
        Disposable request = restApi.getCompany().getCompanies(query, page)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanies, this::handleError);
        unSubscribeOnDestroy(request);
    }

    private void successGetCompanies(CompanyResponse response) {
        getViewState().showProgress(false);
        getViewState().showCompanies(response.getItems(), response.getPages());
    }

    @Override
    protected void handleError(Throwable throwable) {
        super.handleError(throwable);
        getViewState().showProgress(false);
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
