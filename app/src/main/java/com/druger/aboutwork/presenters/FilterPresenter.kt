package com.druger.aboutwork.presenters

import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.interfaces.view.FilterView
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.rx.RxUtils
import moxy.InjectViewState
import org.koin.core.KoinComponent
import org.koin.core.inject


@InjectViewState
class FilterPresenter: BasePresenter<FilterView>(), KoinComponent {

    private val restApi: RestApi by inject()

    private var filterType = FilterType.RATING

    fun setFilterType(filterType: FilterType) {
        this.filterType = filterType
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

    fun applyFilter(position: String, city: String) {
        viewState.applyFilter(filterType, position, city)
    }

    fun clearFilter() {
        viewState.clearClick()
    }
}