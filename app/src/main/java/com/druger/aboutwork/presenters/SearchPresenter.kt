package com.druger.aboutwork.presenters

import com.druger.aboutwork.interfaces.view.SearchView
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
constructor(restApi: RestApi) : BasePresenter<SearchView>() {

    init {
        this.restApi = restApi
    }

    fun getCompanies(query: String, page: Int, withVacancies: Boolean) {
        viewState.showErrorScreen(false)
        viewState.showProgress(true)
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
        viewState.showErrorScreen(true)
    }
}
