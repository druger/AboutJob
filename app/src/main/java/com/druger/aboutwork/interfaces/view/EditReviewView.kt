package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import moxy.MvpView

interface EditReviewView: MvpView {
    fun showVacancies(vacancies: List<Vacancy>)
    fun showCities(cities: List<City>)
    fun showWorkingDate()
    fun setIsIndicatorRatingBar(indicator: Boolean)
    fun showWorkedDate()
    fun showInterviewDate()
    fun clearRatingBar()
    fun successfulEditing()
    fun showErrorEditing()
    fun setupCompanyRating(mark: MarkCompany)
    fun setReview(review: Review)
}