package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.Utils.handleError
import com.druger.aboutwork.utils.rx.RxUtils
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FilterViewModel @Inject constructor(
    private val restApi: RestApi
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var filterType = FilterType.RATING

    val citiesState = MutableLiveData<List<City>>()
    val positionsState = MutableLiveData<List<Vacancy>>()
    val filterState = MutableLiveData<Filter>()
    val clearState = MutableLiveData<Unit>()

    fun setFilterType(filterType: FilterType) {
        this.filterType = filterType
    }

    fun getCities(city: String) {
        val request = restApi.cities.getCities(city)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetCities(it) }, { handleError(it) })
        compositeDisposable.add(request)
    }

    private fun successGetCities(cityResponse: CityResponse) {
        cityResponse.items?.let { citiesState.value = it }
    }

    fun getPositions(position: String) {
        val request = restApi.vacancies.getVacancies(position)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetVacancies(it) }, { handleError(it) })
        compositeDisposable.add(request)
    }

    private fun successGetVacancies(vacancyResponse: VacancyResponse) {
        vacancyResponse.items?.let { positionsState.value = it }
    }

    fun applyFilter(position: String, city: String) {
        filterState.value = Filter(filterType, position, city)
    }

    fun clearFilter() {
        clearState.value = Unit
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    class Filter(
        val filterType: FilterType,
        val position: String,
        val city: String
    )
}