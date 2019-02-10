package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Vacancy

interface EditReviewView: MvpView {
    fun showVacancies(vacancies: List<Vacancy>)
    fun showCities(cities: List<City>)
}