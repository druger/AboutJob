package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserReviewsViewModel @Inject constructor() : ViewModel(), ValueEventListener {

    private lateinit var dbReference: DatabaseReference
    private var valueEventListener: ValueEventListener? = null
    private var nameEventListener: ValueEventListener? = null

    private val reviews: MutableList<Review> = ArrayList()

    val updateReviewState: MutableLiveData<Unit> = MutableLiveData()
    val reviewsState: MutableLiveData<List<Review>> = MutableLiveData()
    val nameState: MutableLiveData<String> = MutableLiveData()

    fun fetchReviews(userId: String) {
        dbReference = FirebaseDatabase.getInstance().reference

        val reviewsQuery = FirebaseHelper.getReviewsById(dbReference, userId)
        reviewsQuery.addValueEventListener(this)
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        fetchReviews(dataSnapshot)
    }

    override fun onCancelled(databaseError: DatabaseError) {
        Timber.e(databaseError.message)
    }

    private fun fetchReviews(dataSnapshot: DataSnapshot) {
        reviews.clear()

        for (snapshot in dataSnapshot.children) {
            val review = snapshot.getValue(Review::class.java)
            val queryCompanies = review?.companyId?.let { FirebaseHelper.getCompanies(dbReference, it) }
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (data in dataSnapshot.children) {
                            val company = data.getValue(Company::class.java)
                            review?.name = company?.name
                            updateReviewState.value = Unit
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Timber.e(databaseError.message)
                }
            }
            queryCompanies?.addValueEventListener(valueEventListener as ValueEventListener)
            review?.firebaseKey = snapshot.key
            review?.let { reviews.add(it) }
        }
        reviewsState.value = reviews
    }

    fun removeListeners() {
        dbReference.removeEventListener(this)
        valueEventListener?.let { dbReference.removeEventListener(it) }
        nameEventListener?.let { dbReference.removeEventListener(it) }
    }

    fun getUserName(id: String) {
        getName(id)
    }

    private fun getName(id: String) {
        val queryUser = FirebaseHelper.getUser(dbReference, id)
        nameEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.name?.let { nameState.value = it }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.message)
            }
        }
        queryUser.addValueEventListener(nameEventListener as ValueEventListener)
    }
}