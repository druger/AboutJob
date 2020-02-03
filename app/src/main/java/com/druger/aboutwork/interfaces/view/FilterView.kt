package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Vacancy
import moxy.MvpView

interface FilterView : MvpView {
    fun showCities(cities: List<City>)
    fun showPositions(positions: List<Vacancy>)
}