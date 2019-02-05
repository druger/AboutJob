package com.druger.aboutwork.presenters

import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.AddReviewView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.rx.RxUtils
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import javax.inject.Inject

@InjectViewState
class AddReviewPresenter @Inject constructor(): BasePresenter<AddReviewView>() {

    private val NOT_SELECTED_STATUS = -1
    private val WORKING_STATUS = 0
    private val WORKED_STATUS = 1
    private val INTERVIEW_STATUS = 2

    private var status = NOT_SELECTED_STATUS

    lateinit var companyDetail: CompanyDetail

    lateinit var review: Review
    private lateinit var mark: MarkCompany

    fun setupReview() {
        val user = FirebaseAuth.getInstance().currentUser
        val companyId = companyDetail.id
        review = Review(companyId, user?.uid, Calendar.getInstance().timeInMillis)
        mark = MarkCompany(user?.uid, companyId)
        review.markCompany = mark
    }

    fun doneClick() {
        val company = Company(companyDetail.id, companyDetail.name)
        addReview(company)
    }

    private fun addReview(company: Company) {
        if (isCorrectStatus() && isCorrectReview(review)) {
            review.status = status
            FirebaseHelper.addReview(review)
            FirebaseHelper.addCompany(company)
            viewState.successfulAddition()
        } else {
            viewState.showErrorAdding()
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

    fun getVacancies(vacancy: String) {
        val request = restApi.vacancies.getVacancies(vacancy)
                .compose(RxUtils.httpSchedulers())
                .subscribe({ successGetVacancies(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)
    }

    private fun successGetVacancies(vacancyResponse: VacancyResponse) {
        viewState.showVacancies(vacancyResponse.items)
    }

    fun getCities(city: String) {
        queryGetCities(city)
    }

    private fun queryGetCities(city: String) {
        val request = restApi.cities.getCities(city)
                .compose(RxUtils.httpSchedulers())
                .subscribe({ this.successGetCities(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)
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