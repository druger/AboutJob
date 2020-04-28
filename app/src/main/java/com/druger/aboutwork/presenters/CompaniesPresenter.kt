package com.druger.aboutwork.presenters

import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.CompaniesView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.google.firebase.database.*
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
class CompaniesPresenter : BasePresenter<CompaniesView>() {

    private lateinit var dbReference: DatabaseReference
    private var reviewEventListener: ValueEventListener? = null
    private var companyEventListener: ValueEventListener? = null

    private var reviews = ArrayList<Review>()

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
                        val review = snapshot.getValue(Review::class.java)
                        review?.let {
                            it.firebaseKey = snapshot.key
                            getCompanies(it)
                        }
                    }
                    viewState.showProgress(false)
                    reviews.reverse()
                    viewState.showReviews(reviews)
                } else {
                    viewState.showProgress(false)
                    viewState.showEmptyReviews()
                }
            }
        }
        reviewsQuery.addValueEventListener(reviewEventListener as ValueEventListener)
    }

    private fun getCompanies(review: Review?) {
        review?.companyId?.let { companyId ->
            val queryCompanies = FirebaseHelper.getCompany(dbReference, companyId)
            companyEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val company = dataSnapshot.getValue(Company::class.java)
                        review.name = company?.name
                        viewState.updateAdapter()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Timber.e(databaseError.message)
                    viewState.showProgress(false)
                }
            }
            queryCompanies.addValueEventListener(companyEventListener as ValueEventListener)
            reviews.add(review)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        reviewEventListener?.let { dbReference.removeEventListener(it) }
        companyEventListener?.let { dbReference.removeEventListener(it) }
    }
}
