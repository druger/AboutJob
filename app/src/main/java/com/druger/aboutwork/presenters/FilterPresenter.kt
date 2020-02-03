package com.druger.aboutwork.presenters

import com.druger.aboutwork.App
import com.druger.aboutwork.interfaces.view.FilterView
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.rx.RxUtils
import javax.inject.Inject

class FilterPresenter @Inject constructor(restApi: RestApi) : BasePresenter<FilterView>() {

    init {
        this.restApi = restApi
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        App.appComponent.inject(this)
    }

    fun filterAtRating() {

    }

    fun filterAtSalary() {

    }

    fun filterAtChief() {

    }

    fun filterAtWorkplace() {

    }

    fun filterAtCareer() {

    }

    fun filterAtCollective() {

    }

    fun filterAtBenefits() {

    }

    fun filterAtPopularity() {

    }

    fun getCities(city: String) {
        val request = restApi.cities.getCities(city)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetCities(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)
    }

    private fun successGetCities(cityResponse: CityResponse) {
        cityResponse.items?.let { viewState.showCities(cityResponse.items) }
    }

    fun getPositions(position: String) {
        val request = restApi.vacancies.getVacancies(position)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetVacancies(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)
    }

    private fun successGetVacancies(vacancyResponse: VacancyResponse) {
        vacancyResponse.items?.let { viewState.showPositions(vacancyResponse.items) }
    }
}