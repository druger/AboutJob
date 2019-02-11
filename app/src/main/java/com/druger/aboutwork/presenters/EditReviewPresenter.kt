package com.druger.aboutwork.presenters

import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.Const
import com.druger.aboutwork.Const.ReviewStatus.*
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

    private var status = NOT_SELECTED_STATUS

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

    private fun isCorrectReview(review: Review): Boolean {
        return (!TextUtils.isEmpty(review.pluses) && !TextUtils.isEmpty(review.minuses)
                && !TextUtils.isEmpty(review.position) && !TextUtils.isEmpty(review.city))
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

    fun onSelectedWorkingStatus(position: Int) {
        viewState.showWorkingDate()

        status = position
        viewState.setIsIndicatorRatingBar(false)
    }

    fun onSelectedWorkedStatus(position: Int) {
        viewState.showWorkedDate()

        status = position
        viewState.setIsIndicatorRatingBar(false)
    }

    fun onSelectedInterviewStatus(position: Int) {
        viewState.showInterviewDate()

        status = position
        viewState.setIsIndicatorRatingBar(true)
        viewState.clearRatingBar()
    }
}