package com.druger.aboutwork.presenters

import com.druger.aboutwork.db.RealmHelper
import com.druger.aboutwork.interfaces.view.SearchView
import com.druger.aboutwork.model.realm.CompanyRealm
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CompanyResponse
import com.druger.aboutwork.utils.rx.RxUtils
import moxy.InjectViewState
import javax.inject.Inject

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
class SearchPresenter @Inject
constructor(restApi: RestApi, realmHelper: RealmHelper) : BasePresenter<SearchView>() {

    init {
        this.restApi = restApi
        this.realmHelper = realmHelper
    }

    fun getCompanies(query: String, page: Int, withVacancies: Boolean) {
        requestGetCompanies(query, page, withVacancies)
    }

    private fun requestGetCompanies(query: String, page: Int, withVacancies: Boolean) {
        val request = restApi.company.getCompanies(query, page, withVacancies)
            .compose(RxUtils.singleTransformers())
            .subscribe({ successGetCompanies(it, query) }, { handleError(it) })
        unSubscribeOnDestroy(request)
    }

    private fun successGetCompanies(response: CompanyResponse, query: String) {
        viewState.showProgress(false)
        val filteredList = response.items?.filter { it.name.contains(query, true) }
        filteredList?.let { viewState.showCompanies(it, response.pages) }
    }

    override fun handleError(throwable: Throwable) {
        super.handleError(throwable)
        viewState.showProgress(false)
    }

    fun saveCompanyToDb(company: CompanyRealm) {
        realmHelper.saveCompany(company)
    }
}