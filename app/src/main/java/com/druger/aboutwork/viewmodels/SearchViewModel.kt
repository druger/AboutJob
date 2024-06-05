package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CompanyResponse
import com.druger.aboutwork.states.CompaniesState
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.utils.rx.RxUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val restApi: RestApi
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val progressState: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val errorState: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val companiesState: MutableLiveData<CompaniesState> = MutableLiveData<CompaniesState>()

    fun getCompanies(query: String, page: Int, withVacancies: Boolean) {
        errorState.value = false
        progressState.value = true
        requestGetCompanies(query, page, withVacancies)
    }

    private fun requestGetCompanies(query: String, page: Int, withVacancies: Boolean) {
        val request = restApi.company.getCompanies(query, page, withVacancies)
            .compose(RxUtils.singleTransformers())
            .subscribe({ successGetCompanies(it, query) }, { handleError(it) })
        compositeDisposable.add(request)
    }

    private fun successGetCompanies(response: CompanyResponse, query: String) {
        progressState.value = false
        val filteredList = response.items?.filter { it.name.contains(query, true) }
        filteredList?.let {
            companiesState.value = CompaniesState(it, response.pages)
        }
    }

    private fun handleError(throwable: Throwable) {
        Utils.handleError(throwable)
        progressState.value = false
        errorState.value = true
    }
}