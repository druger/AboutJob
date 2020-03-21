package com.druger.aboutwork.presenters

import com.druger.aboutwork.App
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.MyReviewsView
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.database.*
import moxy.InjectViewState
import moxy.MvpPresenter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
class MyReviewsPresenter @Inject constructor() : MvpPresenter<MyReviewsView>(), ValueEventListener {

    @Inject
    lateinit var analytics: Analytics

    private lateinit var dbReference: DatabaseReference
    private var valueEventListener: ValueEventListener? = null

    private val reviews = ArrayList<Review>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        App.appComponent.inject(this)
    }

    fun fetchReviews(userId: String) {
        viewState.showProgress(true)
        reviews.clear()
        dbReference = FirebaseDatabase.getInstance().reference

        val reviewsQuery = FirebaseHelper.getReviewsById(dbReference, userId)
        reviewsQuery.addValueEventListener(this)
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        fetchReviews(dataSnapshot)
    }

    override fun onCancelled(databaseError: DatabaseError) {
        viewState.showProgress(false)
        Timber.e(databaseError.toException())
    }

    private fun fetchReviews(dataSnapshot: DataSnapshot) {

        if (dataSnapshot.exists()) {
            for (snapshot in dataSnapshot.children) {
                val review = snapshot.getValue(Review::class.java)
                if (!reviews.contains(review)) {
                    review?.companyId?.let { id ->
                        val queryCompanies = FirebaseHelper.getCompanies(dbReference, id)
                        valueEventListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (data in dataSnapshot.children) {
                                        val company = data.getValue(Company::class.java)
                                        review.name = company?.name
                                        viewState.updateAdapter()
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Timber.e(databaseError.message)
                                viewState.showProgress(false)
                            }
                        }
                        queryCompanies.addValueEventListener(valueEventListener as ValueEventListener)
                        review.firebaseKey = snapshot.key
                        reviews.add(review)
                    }
                }
            }
            viewState.showProgress(false)
            viewState.showReviews(reviews)
        } else {
            viewState.showProgress(false)
            viewState.showEmptyReviews()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
    }

    private fun removeListeners() {
        valueEventListener?.let {
            dbReference.removeEventListener(this)
            dbReference.removeEventListener(it)
        }
    }

    fun getReview(position: Int): Review {
        return reviews[position]
    }

    fun addReview(position: Int, review: Review) {
        addToFirebase(review)
    }

    fun removeReview(position: Int) {
        reviews.removeAt(position).firebaseKey?.let { FirebaseHelper.removeReview(it) }
    }

    fun addDeletedReviews(deletedReviews: List<Review>) {
        reviews.addAll(deletedReviews)
    }

    fun addToFirebase(review: Review) {
        FirebaseHelper.addReview(review)
    }

    fun logEvent(event: String) {
        analytics.logEvent(event)
    }
}
