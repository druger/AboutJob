package com.druger.aboutwork.presenters

import android.text.TextUtils
import com.druger.aboutwork.App
import com.druger.aboutwork.Const.ReviewStatus.INTERVIEW_STATUS
import com.druger.aboutwork.Const.ReviewStatus.NOT_SELECTED_STATUS
import com.druger.aboutwork.Const.ReviewStatus.WORKED_STATUS
import com.druger.aboutwork.Const.ReviewStatus.WORKING_STATUS
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.AddReviewView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.Analytics
import com.druger.aboutwork.utils.Analytics.Companion.ADD_PHOTO_CLICK
import com.druger.aboutwork.utils.Analytics.Companion.ADD_REVIEW
import com.druger.aboutwork.utils.Analytics.Companion.SCREEN
import com.druger.aboutwork.utils.rx.RxUtils
import com.google.firebase.auth.FirebaseAuth
import moxy.InjectViewState
import java.util.*
import javax.inject.Inject

@InjectViewState
class AddReviewPresenter @Inject
constructor(restApi: RestApi) : ReviewPresenter<AddReviewView>() {

    @Inject
    lateinit var analytics: Analytics

    private var status = NOT_SELECTED_STATUS

    var companyId: String? = null
    var companyName: String? = null

    lateinit var review: Review
    private var mark: MarkCompany? = null

    init {
        this.restApi = restApi
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        App.appComponent.inject(this)
    }

    fun setupReview() {
        val user = FirebaseAuth.getInstance().currentUser

        review = Review(companyId, user?.uid, Calendar.getInstance().timeInMillis)
        mark = user?.uid?.let { userId ->
            companyId?.let { companyId ->
                MarkCompany(userId, companyId)
            }
        }
        review.markCompany = mark
    }

    fun doneClick(photosCount: Int) {
        analytics.logEvent(Analytics.ADD_REVIEW_CLICK)
        super.photosCount = photosCount
        val company = companyId?.let { companyId -> companyName?.let { name -> Company(companyId, name) } }
        company?.let { addReview(it) }
    }

    private fun addReview(company: Company) {
        if (isCorrectStatus() && isCorrectReview(review)) {
            review.status = status
            if (photosCount > 0) review.hasPhotos = true
            val reviewKey = FirebaseHelper.addReview(review)
            FirebaseHelper.addCompany(company)
            uploadPhotos(reviewKey)
            viewState.successfulAddition()
        } else {
            viewState.showErrorAdding()
        }
    }

    private fun isCorrectStatus(): Boolean =
        ((status == WORKING_STATUS || status == WORKED_STATUS) &&
            mark?.averageMark != 0f) || status == INTERVIEW_STATUS &&
            mark?.averageMark == 0f


    private fun isCorrectReview(review: Review): Boolean =
        !TextUtils.isEmpty(review.pluses) &&
            !TextUtils.isEmpty(review.minuses) &&
            !TextUtils.isEmpty(review.position) &&
            !TextUtils.isEmpty(review.city)

    fun setSalary(rating: Float) {
        mark?.salary = rating
    }

    fun setChief(rating: Float) {
        mark?.chief = rating
    }

    fun setWorkplace(rating: Float) {
        mark?.workplace = rating
    }

    fun setCareer(rating: Float) {
        mark?.career = rating
    }

    fun setCollective(rating: Float) {
        mark?.collective = rating
    }

    fun setSocialPackage(rating: Float) {
        mark?.socialPackage = rating
    }

    fun getVacancies(vacancy: String) {
        val request = restApi.vacancies.getVacancies(vacancy)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetVacancies(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)
    }

    private fun successGetVacancies(vacancyResponse: VacancyResponse) {
        vacancyResponse.items?.let { viewState.showVacancies(vacancyResponse.items) }
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

    fun onSelectedWorkingStatus(position: Int) {
        analytics.logEvent(Analytics.WORKING_STATUS_CLICK)
        viewState.showWorkingDate()

        status = position
        viewState.setIsIndicatorRatingBar(false)
    }

    fun onSelectedWorkedStatus(position: Int) {
        analytics.logEvent(Analytics.WORKED_STATUS_CLICK)
        viewState.showWorkedDate()

        status = position
        viewState.setIsIndicatorRatingBar(false)
    }

    fun onSelectedInterviewStatus(position: Int) {
        analytics.logEvent(Analytics.INTERVIEW_STATUS_CLICK)
        viewState.showInterviewDate()

        status = position
    }

    fun closeClick() {
        analytics.logEvent(Analytics.CLOSE_ADD_REVIEW_CLICK)
    }

    fun employmentDateClick() {
        analytics.logEvent(Analytics.EMPLOYMENT_DATE_CLICK)
    }

    fun dismissalDateClick() {
        analytics.logEvent(Analytics.DISMISSAL_DATE_CLICK)
    }

    fun setRecommendedReview() {
        review.recommended = true
    }

    fun setNotRecommendedReview() {
        review.recommended = false
    }

    fun clearRecommended() {
        review.recommended = null
    }

    fun sendAnalytics() {
        analytics.logEvent(ADD_PHOTO_CLICK, SCREEN, ADD_REVIEW)
    }
}