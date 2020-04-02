package com.druger.aboutwork.interfaces.view

import android.net.Uri
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.Vacancy
import moxy.MvpView

interface AddReviewView: MvpView {
    fun showVacancies(vacancies: List<Vacancy>)
    fun showCities(cities: List<City>)
    fun successfulAddition()
    fun showErrorAdding()
    fun showWorkingDate()
    fun setIsIndicatorRatingBar(indicator: Boolean)
    fun showWorkedDate()
    fun showInterviewDate()
    fun showPhotos(uri: Array<Uri?>)
}