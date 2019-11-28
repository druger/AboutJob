package com.druger.aboutwork.presenters

import android.text.TextUtils
import com.druger.aboutwork.Const.ReviewStatus.INTERVIEW_STATUS
import com.druger.aboutwork.Const.ReviewStatus.NOT_SELECTED_STATUS
import com.druger.aboutwork.Const.ReviewStatus.WORKED_STATUS
import com.druger.aboutwork.Const.ReviewStatus.WORKING_STATUS
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.EditReviewView
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.rx.RxUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class EditReviewPresenter @Inject
constructor(restApi: RestApi) : BasePresenter<EditReviewView>() {

    private var status: Int = NOT_SELECTED_STATUS

    private var review: Review? = null
    private var mark: MarkCompany? = null

    private val dbReference = FirebaseDatabase.getInstance().reference
    private lateinit var reviewListener: ValueEventListener

    init {
        this.restApi = restApi
    }

    fun setupRating(review: Review) {
        this.review = review
        mark = review.markCompany
        this.review?.markCompany = mark
        mark?.let { viewState.setupCompanyRating(it) }
    }

    fun doneClick() {
        updateReview()
    }

    private fun updateReview() {
        review?.let {
            if (isCorrectStatus() && isCorrectReview(it)) {
                it.status = status
                FirebaseHelper.updateReview(it)
                viewState.successfulEditing()
            } else {
                viewState.showErrorEditing()
            }
        }
    }

    private fun isCorrectStatus(): Boolean =
        (status == WORKING_STATUS || status == WORKED_STATUS) &&
            mark?.averageMark != 0f || status == INTERVIEW_STATUS && mark?.averageMark == 0f


    private fun isCorrectReview(review: Review): Boolean {
        return (!TextUtils.isEmpty(review.pluses) && !TextUtils.isEmpty(review.minuses)
            && !TextUtils.isEmpty(review.position) && !TextUtils.isEmpty(review.city))
    }

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

    fun getCities(city: String) {
        val request = restApi.cities.getCities(city)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetCities(it) }, { this.handleError(it) })
        unSubscribeOnDestroy(request)
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

    private fun successGetCities(cityResponse: CityResponse) {
        cityResponse.items?.let { viewState.showCities(cityResponse.items) }
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
    }

    fun getReview(reviewKey: String) {
        val queryReview = FirebaseHelper.getReview(dbReference, reviewKey)
        reviewListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                review = dataSnapshot.getValue(Review::class.java)
                review?.let {
                    it.firebaseKey = dataSnapshot.key
                    viewState.setReview(it)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.message)
            }
        }
        queryReview.addValueEventListener(reviewListener)
    }
}