package com.druger.aboutwork.presenters

import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.enums.FilterType
import com.druger.aboutwork.interfaces.view.CompanyDetailView
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.User
import com.druger.aboutwork.rest.RestApi
import com.druger.aboutwork.utils.rx.RxUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import moxy.InjectViewState
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
class CompanyDetailPresenter @Inject
constructor(restApi: RestApi) : BasePresenter<CompanyDetailView>(), ValueEventListener {

    private lateinit var auth: FirebaseAuth
    private var authListener: FirebaseAuth.AuthStateListener? = null

    private var dbReference: DatabaseReference? = null
    private var valueEventListener: ValueEventListener? = null

    private val reviews = ArrayList<Review>()

    init {
        this.restApi = restApi
    }

    fun getReviews(companyID: String) {
        viewState.showProgress(true)
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

        for (snapshot in dataSnapshot.children) {
            val review = snapshot.getValue(Review::class.java)
            dbReference?.let { db ->
                val queryUser = review?.userId?.let { FirebaseHelper.getUser(db, it) }
                valueEventListener?.let { queryUser?.addValueEventListener(it) }
            }
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (data in dataSnapshot.children) {
                            val user = data.getValue(User::class.java)
                            review?.name = user?.name
                            viewState.updateAdapter()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Timber.e(databaseError.message)
                    viewState.showProgress(false)
                }
            }
            review?.firebaseKey = snapshot.key
            review?.let { reviews.add(it) }
        }
        viewState.showProgress(false)
        reviews.reverse()
        viewState.showReviews(reviews)
    }

    override fun onCancelled(databaseError: DatabaseError) {
        Timber.e(databaseError.message)
        viewState.showProgress(false)
    }

    fun removeListeners() {
        dbReference?.removeEventListener(this)
        valueEventListener?.let { dbReference?.removeEventListener(it) }
    }

    fun getCompanyDetail(companyID: String) {
        viewState.showErrorScreen(false)
        viewState.showProgress(true)
        requestCompanyDetail(companyID)
    }

    private fun requestCompanyDetail(companyID: String) {
        val request = restApi.company.getCompanyDetail(companyID)
            .compose(RxUtils.singleTransformers())
            .subscribe({ this.successGetCompanyDetails(it) }, { this.handleError(it) })

        unSubscribeOnDestroy(request)
    }

    private fun successGetCompanyDetails(companyDetail: CompanyDetail) {
        viewState.showProgress(false)
        viewState.showCompanyDetail(companyDetail)
    }

    override fun handleError(throwable: Throwable) {
        super.handleError(throwable)
        viewState.showProgress(false)
        viewState.showErrorScreen(true)
    }

    fun checkAuthUser() {
        auth = FirebaseAuth.getInstance()
        initAuthListener()
        authListener?.let { auth.addAuthStateListener(it) }
    }

    private fun initAuthListener() {
        authListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) viewState.showAuth()
            else viewState.addReview()
        }
    }

    fun removeAuthListener() {
        authListener?.let { auth.removeAuthStateListener(it) }
    }

    fun filterReviews(filterType: FilterType, position: String, city: String) {
        val filteredReviews = when(filterType) {
            FilterType.RATING -> reviews.sortedByDescending { it.markCompany?.averageMark }
            FilterType.SALARY -> reviews.sortedByDescending { it.markCompany?.salary }
            FilterType.CHIEF -> reviews.sortedByDescending { it.markCompany?.chief }
            FilterType.WORKPLACE -> reviews.sortedByDescending { it.markCompany?.workplace }
            FilterType.CAREER -> reviews.sortedByDescending { it.markCompany?.career }
            FilterType.COLLECTIVE -> reviews.sortedByDescending { it.markCompany?.collective }
            FilterType.BENEFITS -> reviews.sortedByDescending { it.markCompany?.socialPackage }
            FilterType.POPULARITY -> reviews.sortedByDescending { it.like }
        }
        viewState.showReviews(filteredReviews)
    }
}
