package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.EditReviewView
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.rx.RxUtils
import javax.inject.Inject

@InjectViewState
class EditReviewPresenter @Inject constructor(): BasePresenter<EditReviewView>() {

    private lateinit var review: Review
    private lateinit var mark: MarkCompany

    fun setupRating(review: Review) {
         this.review = review
         mark = review.markCompany
         review.markCompany = mark
    }

    fun doneClick() {
        updateReview()
    }

    private fun updateReview() {
        if (isCorrectStatus() && isCorrectReview(review)) {
            review.status = status
            FirebaseHelper.updateReview(review)
            viewState.successfulEditing()
        } else {
            viewState.showErrorEditing()
        }
    }

    private fun isCorrectStatus(): Boolean {
        return (status == WORKING_STATUS || status == WORKED_STATUS) && mark.averageMark != 0F
                || status == INTERVIEW_STATUS && mark.averageMark == 0F
    }

    fun setSalary(rating: Float) {
        mark.salary = rating
    }

    fun setChief(rating: Float) {
        mark.chief = rating
    }

    fun setWorkplace(rating: Float) {
        mark.workplace = rating
    }

    fun setCareer(rating: Float) {
        mark.career = rating
    }

    fun setCollective(rating: Float) {
        mark.collective = rating
    }

    fun setSocialPackage(rating: Float) {
        mark.socialPackage = rating
    }

    fun getCities(city: String) {
        val request = restApi.cities.getCities(city)
                .compose(RxUtils.httpSchedulers())
                .subscribe({ successGetCities(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)

    }

    fun getVacancies(vacancy: String) {
        val request = restApi.vacancies.getVacancies(vacancy)
                .compose(RxUtils.httpSchedulers())
                .subscribe({ successGetVacancies(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)

    }

    private fun successGetVacancies(vacancyResponse: VacancyResponse) {
        viewState.showVacancies(vacancyResponse.items)
    }

    private fun successGetCities(cityResponse: CityResponse) {
        viewState.showCities(cityResponse.items)
    }

}