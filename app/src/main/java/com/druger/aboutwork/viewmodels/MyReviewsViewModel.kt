package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyReviewsViewModel @Inject constructor(
    private val analytics: Analytics
) : ViewModel(), ValueEventListener {

    private lateinit var dbReference: DatabaseReference
    private var valueEventListener: ValueEventListener? = null

    private val reviews = ArrayList<Review>()

    val progressState: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val adapterState: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val reviewState: MutableLiveData<List<Review>> = MutableLiveData<List<Review>>()
    val emptyReviewsState: MutableLiveData<Unit> = MutableLiveData<Unit>()

    fun fetchReviews(userId: String) {
        progressState.value = true
        reviews.clear()
        dbReference = FirebaseDatabase.getInstance().reference

        val reviewsQuery = FirebaseHelper.getReviewsById(dbReference, userId)
        reviewsQuery.addValueEventListener(this)
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

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        fetchReviews(dataSnapshot)
    }

    override fun onCancelled(error: DatabaseError) {
        progressState.value = false
        Timber.e(error.toException())
    }

    override fun onCleared() {
        super.onCleared()
        removeListeners()
    }

    private fun removeListeners() {
        valueEventListener?.let {
            dbReference.removeEventListener(this)
            dbReference.removeEventListener(it)
        }
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
                                        adapterState.value = Unit
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Timber.e(databaseError.message)
                                progressState.value = false
                            }
                        }
                        queryCompanies.addValueEventListener(valueEventListener as ValueEventListener)
                        review.firebaseKey = snapshot.key
                        reviews.add(review)
                    }
                }
            }
            progressState.value = false
            reviewState.value = reviews
        } else {
            progressState.value = false
            emptyReviewsState.value = Unit
        }
    }
}