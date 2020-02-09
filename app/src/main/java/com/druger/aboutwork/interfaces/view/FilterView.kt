package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Vacancy
import moxy.MvpView

interface FilterView : MvpView {
    fun showCities(cities: List<City>)
    fun showPositions(positions: List<Vacancy>)
    fun applyFilter(filterType: FilterType, position: String, city: String)
}