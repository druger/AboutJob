package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.R
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.User
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.utils.Utils
import com.druger.aboutwork.utils.rx.RxUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CompanyDetailViewModel @Inject constructor(
    private val restApi: RestApi
) : ViewModel(), ValueEventListener {

    private lateinit var auth: FirebaseAuth
    private var authListener: FirebaseAuth.AuthStateListener? = null

    private val compositeDisposable = CompositeDisposable()

    private var dbReference: DatabaseReference? = null
    private var valueEventListener: ValueEventListener? = null

    private val reviews = ArrayList<Review>()
    private var position: String = ""
    private var city: String = ""
    private var filterType = FilterType.RATING

    val progressState: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val errorState: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val adapterState: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val reviewState: MutableLiveData<List<Review>> = MutableLiveData<List<Review>>()
    val emptyReviewsState: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val emptyFilteredReviewsState: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val filterIconState: MutableLiveData<Int> = MutableLiveData<Int>()
    val authState: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val addReviewState: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val companyDetailState: MutableLiveData<CompanyDetail> = MutableLiveData<CompanyDetail>()
    val filterState: MutableLiveData<Filter> = MutableLiveData<Filter>()

    fun getReviews(companyID: String) {
        progressState.value = true
        dbReference = FirebaseDatabase.getInstance().reference
        dbReference?.let {
            val reviewsQuery = FirebaseHelper.getReviewsForCompany(it, companyID)
            reviewsQuery.addValueEventListener(this)
        }
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        fetchReviews(dataSnapshot)
    }

    private fun fetchReviews(dataSnapshot: DataSnapshot) {
        reviews.clear()

        if (dataSnapshot.exists()) {
            for (snapshot in dataSnapshot.children) {
                val review = snapshot.getValue(Review::class.java)
                dbReference?.let { db ->
                    val queryUser = review?.userId?.let { FirebaseHelper.getUser(db, it) }
                    valueEventListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (data in dataSnapshot.children) {
                                    val user = data.getValue(User::class.java)
                                    review?.name = user?.name
                                    adapterState.value = Unit
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Timber.e(databaseError.message)
                            progressState.value = false
                        }
                    }
                    valueEventListener?.let { queryUser?.addValueEventListener(it) }
                }
                review?.firebaseKey = snapshot.key
                review?.let { reviews.add(it) }
            }
            progressState.value = false
            reviews.reverse()
            reviewState.value = reviews
            checkFilterSettings()
        } else {
            progressState.value = false
            emptyReviewsState.value = Unit
        }
    }

    private fun checkFilterSettings() {
        if (position.isNotEmpty() || city.isNotEmpty()) {
            filterReviews(filterType, position, city)
            filterIconState.value = R.drawable.ic_filter_applied
        } else {
            filterIconState.value = R.drawable.ic_filter
        }
    }

    override fun onCancelled(databaseError: DatabaseError) {
        Timber.e(databaseError.message)
        progressState.value = false
    }

    fun removeListeners() {
        dbReference?.removeEventListener(this)
        valueEventListener?.let { dbReference?.removeEventListener(it) }
    }

    fun getCompanyDetail(companyID: String) {
        errorState.value = false
        progressState.value = true
        requestCompanyDetail(companyID)
    }

    private fun requestCompanyDetail(companyID: String) {
        val request = restApi.company.getCompanyDetail(companyID)
            .compose(RxUtils.singleTransformers())
            .subscribe({ this.successGetCompanyDetails(it) }, { handleError(it) })
        compositeDisposable.add(request)
    }

    private fun successGetCompanyDetails(companyDetail: CompanyDetail) {
        progressState.value = false
        companyDetailState.value = companyDetail
    }

    private fun handleError(throwable: Throwable) {
        Utils.handleError(throwable)
        progressState.value = false
        errorState.value = true
    }

    fun checkAuthUser() {
        auth = FirebaseAuth.getInstance()
        initAuthListener()
        authListener?.let { auth.addAuthStateListener(it) }
    }

    private fun initAuthListener() {
        authListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) authState.value = Unit
            else addReviewState.value = Unit
        }
    }

    fun removeAuthListener() {
        authListener?.let { auth.removeAuthStateListener(it) }
    }

    fun filterReviews(filterType: FilterType, position: String, city: String) {
        this.position = position
        this.city = city
        val sortedReviews = when (filterType) {
            FilterType.RATING -> reviews.sortedByDescending { it.markCompany?.averageMark }
            FilterType.SALARY -> reviews.sortedByDescending { it.markCompany?.salary }
            FilterType.CHIEF -> reviews.sortedByDescending { it.markCompany?.chief }
            FilterType.WORKPLACE -> reviews.sortedByDescending { it.markCompany?.workplace }
            FilterType.CAREER -> reviews.sortedByDescending { it.markCompany?.career }
            FilterType.COLLECTIVE -> reviews.sortedByDescending { it.markCompany?.collective }
            FilterType.BENEFITS -> reviews.sortedByDescending { it.markCompany?.socialPackage }
            FilterType.POPULARITY -> reviews.sortedByDescending { it.like }
        }
        filterIconState.value = R.drawable.ic_filter_applied
        if (position.isEmpty() && city.isEmpty()) {
            if (sortedReviews.isNotEmpty()) reviewState.value = sortedReviews
            else emptyReviewsState.value = Unit
            return
        }
        var filteredReviews = emptyList<Review>()
        if (position.isNotEmpty() && city.isNotEmpty()) {
            filteredReviews = sortedReviews
                .filter { it.position.equals(position, true) }
                .filter { it.city.equals(city, true) }
        } else if (position.isNotEmpty() && city.isEmpty()) {
            filteredReviews = sortedReviews
                .filter { it.position.equals(position, true) }
        } else if (position.isEmpty() && city.isNotEmpty()) {
            filteredReviews = sortedReviews
                .filter { it.city.equals(city, true) }
        }
        if (filteredReviews.isNotEmpty()) reviewState.value = filteredReviews
        else emptyFilteredReviewsState.value = Unit
    }

    fun filterClick() {
        filterState.value = Filter(position, city)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    data class Filter(val position: String, val city: String)
}