package com.druger.aboutwork.viewmodels

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.Const
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.*
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.Analytics
import com.druger.aboutwork.utils.UploadPhotoHelper.uploadPhotos
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.utils.rx.RxUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddReviewVieModel @Inject constructor(
    private val analytics: Analytics,
    private val restApi: RestApi
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var status = Const.ReviewStatus.NOT_SELECTED_STATUS

    private var mark: MarkCompany? = null

    val successState: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    val errorState: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    val vacancies: MutableLiveData<List<Vacancy>> by lazy {
        MutableLiveData<List<Vacancy>>()
    }

    val cities: MutableLiveData<List<City>> by lazy {
        MutableLiveData<List<City>>()
    }

    val workingDate: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    val workedDate: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    val interviewDate: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    var companyId: String? = null
    var companyName: String? = null

    lateinit var review: Review

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

    fun doneClick(photos: MutableList<Uri?>, wasPhotoRemoved: Boolean) {
        analytics.logEvent(Analytics.ADD_REVIEW_CLICK)
        val company =
            companyId?.let { companyId -> companyName?.let { name -> Company(companyId, name) } }
        company?.let { addReview(it, photos, wasPhotoRemoved) }
    }

    private fun addReview(company: Company, photos: MutableList<Uri?>, wasPhotoRemoved: Boolean) {
        if (isCorrectStatus() && isCorrectReview(review)) {
            review.status = status
            if (photos.isNotEmpty()) review.hasPhotos = true
            val reviewKey = FirebaseHelper.addReview(review)
            FirebaseHelper.addCompany(company)
            if (wasPhotoRemoved) uploadPhotos(reviewKey, photos)
            else uploadPhotos(reviewKey)
            successState.value = Unit
        } else {
            errorState.value = Unit
        }
    }

    private fun isCorrectStatus(): Boolean =
        ((status == Const.ReviewStatus.WORKING_STATUS || status == Const.ReviewStatus.WORKED_STATUS) &&
            mark?.averageMark != 0f) || status == Const.ReviewStatus.INTERVIEW_STATUS &&
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
            .subscribe({ this.successGetVacancies(it) }, { Utils.handleError(it) })
        compositeDisposable.add(request)
    }

    private fun successGetVacancies(vacancyResponse: VacancyResponse) {
        vacancyResponse.items?.let { vacancies.value = vacancyResponse.items }
    }

    fun getCities(city: String) {
        val request = restApi.cities.getCities(city)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetCities(it) }, { Utils.handleError(it) })
        compositeDisposable.add(request)
    }

    private fun successGetCities(cityResponse: CityResponse) {
        cityResponse.items?.let { cities.value = cityResponse.items }
    }

    fun onSelectedWorkingStatus(position: Int) {
        analytics.logEvent(Analytics.WORKING_STATUS_CLICK)
        workingDate.value = Unit

        status = position
    }

    fun onSelectedWorkedStatus(position: Int) {
        analytics.logEvent(Analytics.WORKED_STATUS_CLICK)
        workedDate.value = Unit

        status = position
    }

    fun onSelectedInterviewStatus(position: Int) {
        analytics.logEvent(Analytics.INTERVIEW_STATUS_CLICK)
        interviewDate.value = Unit

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
        analytics.logEvent(Analytics.ADD_PHOTO_CLICK, Analytics.SCREEN, Analytics.ADD_REVIEW)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}