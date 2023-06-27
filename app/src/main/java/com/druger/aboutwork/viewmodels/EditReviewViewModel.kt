package com.druger.aboutwork.viewmodels

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.Const
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.City
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.Vacancy
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.rest.models.CityResponse
import com.druger.aboutwork.rest.models.VacancyResponse
import com.druger.aboutwork.utils.Analytics
import com.druger.aboutwork.utils.UploadPhotoHelper.uploadPhotos
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.utils.rx.RxUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditReviewViewModel @Inject constructor(
    private val analytics: Analytics,
    private val restApi: RestApi
) : ViewModel() {

    private var status: Int = Const.ReviewStatus.NOT_SELECTED_STATUS

    private var review: Review? = null
    private var mark: MarkCompany? = null

    private val dbReference = FirebaseDatabase.getInstance().reference
    private lateinit var reviewListener: ValueEventListener

    private val compositeDisposable = CompositeDisposable()

    val companyRatingState: MutableLiveData<MarkCompany> = MutableLiveData<MarkCompany>()
    val successEditing: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val errorEditing: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val vacanciesState: MutableLiveData<List<Vacancy>> = MutableLiveData<List<Vacancy>>()
    val citiesState: MutableLiveData<List<City>> = MutableLiveData<List<City>>()
    val workingDate: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val workedDate: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val interviewDate: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val reviewState: MutableLiveData<Review> = MutableLiveData<Review>()
    val photosState: MutableLiveData<List<StorageReference>> =
        MutableLiveData<List<StorageReference>>()
    val indicatorRatingBar: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun setupRating(review: Review) {
        this.review = review
        mark = review.markCompany
        this.review?.markCompany = mark
        mark?.let { companyRatingState.value = it }
    }

    fun doneClick() {
        updateReview()
    }

    private fun updateReview() {
        review?.let {
            if (isCorrectStatus() && isCorrectReview(it)) {
                it.status = status
                FirebaseHelper.updateReview(it)
                uploadPhotos(it.firebaseKey)
                successEditing.value = Unit
            } else {
                errorEditing.value = Unit
            }
        }
    }

    private fun isCorrectStatus(): Boolean =
        (status == Const.ReviewStatus.WORKING_STATUS || status == Const.ReviewStatus.WORKED_STATUS) &&
            mark?.averageMark != 0f || status == Const.ReviewStatus.INTERVIEW_STATUS && mark?.averageMark == 0f


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
            .subscribe({ this.successGetCities(it) }, { Utils.handleError(it) })
        compositeDisposable.add(request)
    }

    fun getVacancies(vacancy: String) {
        val request = restApi.vacancies.getVacancies(vacancy)
            .compose(RxUtils.observableTransformer())
            .subscribe({ this.successGetVacancies(it) }, { Utils.handleError(it) })
        compositeDisposable.add(request)
    }

    private fun successGetVacancies(vacancyResponse: VacancyResponse) {
        vacancyResponse.items?.let { vacanciesState.value = it }
    }

    private fun successGetCities(cityResponse: CityResponse) {
        cityResponse.items?.let { citiesState.value = it }
    }

    fun onSelectedWorkingStatus(position: Int) {
        workingDate.value = Unit
        status = position
        indicatorRatingBar.value = false
    }

    fun onSelectedWorkedStatus(position: Int) {
        workedDate.value = Unit
        status = position
        indicatorRatingBar.value = false
    }

    fun onSelectedInterviewStatus(position: Int) {
        interviewDate.value = Unit
        status = position
    }

    fun getReview(reviewKey: String) {
        val queryReview = FirebaseHelper.getReview(dbReference, reviewKey)
        reviewListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                review = dataSnapshot.getValue(Review::class.java)
                review?.let {
                    it.firebaseKey = dataSnapshot.key
                    reviewState.value = it
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.message)
            }
        }
        queryReview.addValueEventListener(reviewListener)
    }

    fun setRecommendedReview() {
        review?.recommended = true
    }

    fun setNotRecommendedReview() {
        review?.recommended = false
    }

    fun clearRecommended() {
        review?.recommended = null
    }

    fun getPhotos(reviewId: String) {
        val storageRef = Firebase.storage.reference
        val path = FirebaseHelper.REVIEW_PHOTOS + reviewId
        storageRef.child(path).listAll()
            .addOnSuccessListener {
                if (it.items.isNotEmpty()) photosState.value = it.items
            }
            .addOnFailureListener { Timber.e(it) }
    }

    fun sendAnalytics() {
        analytics.logEvent(Analytics.ADD_PHOTO_CLICK, Analytics.SCREEN, Analytics.EDIT_REVIEW)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}