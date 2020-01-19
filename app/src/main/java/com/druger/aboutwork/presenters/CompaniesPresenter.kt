package com.druger.aboutwork.presenters

import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.CompaniesView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.rest.RestApi
import com.google.firebase.database.*
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
class CompaniesPresenter @Inject
constructor(restApi: RestApi) : BasePresenter<CompaniesView>() {

    private lateinit var dbReference: DatabaseReference
    private var reviewEventListener: ValueEventListener? = null
    private var companyEventListener: ValueEventListener? = null

    private var reviews = ArrayList<Review>()

    init {
        this.restApi = restApi
    }

    override fun handleError(throwable: Throwable) {
        super.handleError(throwable)
        viewState.showProgress(false)
    }

    fun fetchReviews() {
        dbReference = FirebaseDatabase.getInstance().reference
        viewState.showProgress(true)
        reviews.clear()
        getReviews()
    }

    private fun getReviews() {
        val reviewsQuery = FirebaseHelper.getLastReviews(dbReference)
        reviewEventListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                viewState.showProgress(false)
                Timber.e(databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        snapshot.getValue(Review::class.java)?.let { review ->
                            review.firebaseKey = snapshot.key
                            reviews.add(review)
                        }
                    }
                    getCompanies()
                } else {
                    viewState.showProgress(false)
                    viewState.showEmptyReviews()
                }
            }
        }
        reviewsQuery.addValueEventListener(reviewEventListener as ValueEventListener)
    }

    private fun getCompanies() {
        for (review in reviews) {
            review.companyId?.let { companyId ->
                val queryCompanies = FirebaseHelper.getCompany(dbReference, companyId)
                companyEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val company = dataSnapshot.getValue(Company::class.java)
                            review.name = company?.name
                        }
                        viewState.showProgress(false)
                        viewState.showReview(review)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Timber.e(databaseError.message)
                        viewState.showProgress(false)
                    }
                }
                queryCompanies.addValueEventListener(companyEventListener as ValueEventListener)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        reviewEventListener?.let { dbReference.removeEventListener(it) }
        companyEventListener?.let { dbReference.removeEventListener(it) }
    }
}
