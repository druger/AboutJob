package com.druger.aboutwork.presenters

import com.druger.aboutwork.db.RealmHelper
import com.druger.aboutwork.interfaces.view.CompaniesView
import com.druger.aboutwork.model.realm.CompanyRealm
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CompanyResponse
import com.druger.aboutwork.utils.rx.RxUtils
import io.realm.OrderedCollectionChangeSet.State.INITIAL
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmResults
import moxy.InjectViewState
import javax.inject.Inject

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
class CompaniesPresenter @Inject
constructor(restApi: RestApi, realmHelper: RealmHelper) : BasePresenter<CompaniesView>() {

    private lateinit var companies: RealmResults<CompanyRealm>

    private val realmCallback: OrderedRealmCollectionChangeListener<RealmResults<CompanyRealm>> =
        OrderedRealmCollectionChangeListener { companies, changeSet ->
            if (companies.size > 0 && changeSet.state == INITIAL) {
                viewState.showWatchedRecently()
                viewState.showCompaniesRealm()
            }
        }

    init {
        this.restApi = restApi
        this.realmHelper = realmHelper
    }

    fun getCompaniesFromDb(): RealmResults<CompanyRealm> {
        companies = realmHelper.getCompanies()
        companies.addChangeListener(realmCallback)
        return companies
    }

    fun getCompanies(query: String, page: Int) {
        requestGetCompanies(query, page)
    }

    private fun requestGetCompanies(query: String, page: Int) {
        val request = restApi.company.getCompanies(query, page)
            .compose(RxUtils.singleTransformers())
            .subscribe({ successGetCompanies(it) }, { handleError(it) })
        unSubscribeOnDestroy(request)
    }

    private fun successGetCompanies(response: CompanyResponse) {
        viewState.showProgress(false)
        response.items?.let { viewState.showCompanies(it, response.pages) }
    }

    override fun handleError(throwable: Throwable) {
        super.handleError(throwable)
        viewState.showProgress(false)
    }

    fun saveCompanyToDb(company: CompanyRealm) {
        realmHelper.saveCompany(company)
    }

    fun removeRealmListener() {
        companies.removeChangeListener(realmCallback)
    }
}
