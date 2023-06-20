package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

class CompaniesViewModel: ViewModel() {
    private lateinit var dbReference: DatabaseReference
    private var reviewEventListener: ValueEventListener? = null
    private var companyEventListener: ValueEventListener? = null

    private var reviews = ArrayList<Review>()

    val progress: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val emptyReviews: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val updateAdapter: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val reviewsState: MutableLiveData<List<Review>> = MutableLiveData<List<Review>>()

    fun fetchReviews() {
        dbReference = FirebaseDatabase.getInstance().reference
        progress.value = true
        reviews.clear()
        getReviews()
    }

    private fun getReviews() {
        val reviewsQuery = FirebaseHelper.getLastReviews(dbReference)
        reviewEventListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                progress.value = false
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
                    progress.value = false
                    reviews.reverse()
                    reviewsState.value = reviews
                } else {
                    progress.value = false
                    emptyReviews.value = Unit
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
                        updateAdapter.value = Unit
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Timber.e(databaseError.message)
                    progress.value = false
                }
            }
            queryCompanies.addValueEventListener(companyEventListener as ValueEventListener)
            reviews.add(review)
        }
    }

    override fun onCleared() {
        super.onCleared()
        reviewEventListener?.let { dbReference.removeEventListener(it) }
        companyEventListener?.let { dbReference.removeEventListener(it) }
    }
}