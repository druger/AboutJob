package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.R
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.User
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SelectedReviewViewModel @Inject constructor(
    private val analytics: Analytics
) : ViewModel(), ValueEventListener {

    val clearMessageState: MutableLiveData<Unit> = MutableLiveData()
    val authState: MutableLiveData<Int> = MutableLiveData()
    val changeDialogState: MutableLiveData<Int> = MutableLiveData()
    val deleteCommentState: MutableLiveData<Int> = MutableLiveData()
    val commentsState: MutableLiveData<List<Comment>> = MutableLiveData()
    val reviewState: MutableLiveData<Review?> = MutableLiveData()
    val likeClickState: MutableLiveData<Unit> = MutableLiveData()
    val dislikeClickState: MutableLiveData<Unit> = MutableLiveData()
    val userReviewsState: MutableLiveData<String?> = MutableLiveData()
    val companyDetailsState: MutableLiveData<String?> = MutableLiveData()
    val photosState: MutableLiveData<List<StorageReference>> = MutableLiveData()

    var user: FirebaseUser? = null
    private var dbReference = FirebaseDatabase.getInstance().reference
    private var reviewListener: ValueEventListener? = null
    private var nameListener: ValueEventListener? = null

    var comments: List<Comment> = emptyList()
        private set

    lateinit var comment: Comment
    private var review: Review? = null

    init {
        user = FirebaseAuth.getInstance().currentUser
    }

    fun addComment(message: String, reviewId: String) {
        if (user != null) {
            val calendar = Calendar.getInstance()
            val comment = Comment(message, calendar.timeInMillis)
            comment.userId = user?.uid
            comment.userName = user?.displayName
            comment.reviewId = reviewId
            FirebaseHelper.addComment(comment)
            clearMessageState.value = Unit
            analytics.logEvent(Analytics.ADD_COMMENT)
        } else {
            authState.value = R.string.comment_login
        }
    }

    fun updateComment(message: String) {
        FirebaseHelper.updateComment(comment.id, message)
        clearMessageState.value = Unit
        analytics.logEvent(Analytics.UPDATE_COMMENT)
    }

    fun onLongClick(position: Int): Boolean {
        comment = comments[position]
        if (comment.userId == user?.uid) {
            changeDialogState.value = position
            analytics.logEvent(Analytics.LONG_CLICK_MY_COMMENT)
            return true
        }
        return false
    }

    fun deleteComment(position: Int) {
        FirebaseHelper.deleteComment(comment.id)
        comments = comments.toMutableList().apply { removeAt(position) }
        deleteCommentState.value = position
        analytics.logEvent(Analytics.DELETE_COMMENT)
    }

    fun retrieveComments(reviewId: String) {
        val commentsQuery = FirebaseHelper.getComments(dbReference, reviewId)
        commentsQuery.addValueEventListener(this)
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        comments = comments.toMutableList().apply { clear() }
        for (snapshot in dataSnapshot.children) {
            val comment = snapshot.getValue(Comment::class.java)
            comment?.id = snapshot.key.toString()
            comments = comments.toMutableList().apply { comment?.let { add(it) } }
        }
        commentsState.value = comments.reversed()
    }

    override fun onCancelled(p0: DatabaseError) {}

    fun removeListeners() {
        dbReference.removeEventListener(this)
        reviewListener?.let { dbReference.removeEventListener(it) }
    }

    fun getReview(reviewKey: String, showUserName: Boolean) {
        val queryReview = FirebaseHelper.getReview(dbReference, reviewKey)
        reviewListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                review = snapshot.getValue(Review::class.java)
                review?.firebaseKey = snapshot.key
                if (showUserName) getUserName()
                else getCompany()
            }
        }
        queryReview.addValueEventListener(reviewListener as ValueEventListener)
    }

    private fun getUserName() {
        val queryUser = review?.userId?.let { FirebaseHelper.getUser(dbReference, it) }
        nameListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val user = data.getValue(User::class.java)
                    review?.name = user?.name
                    reviewState.value = review
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.message)
            }
        }
        queryUser?.addValueEventListener(nameListener as ValueEventListener)
    }

    private fun getCompany() {
        val queryCompany = review?.let { review ->
            review.companyId?.let { id ->
                FirebaseHelper.getCompany(dbReference, id)
            }
        }
        nameListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val company = snapshot.getValue(Company::class.java)
                review?.name = company?.name
                reviewState.value = review
            }
        }
        queryCompany?.addValueEventListener(nameListener as ValueEventListener)
    }

    fun clickLike() {
        if (user == null) authState.value = R.string.like_login
        else likeClickState.value = Unit
    }

    fun clickDislike() {
        if (user == null) authState.value = R.string.dislike_login
        else dislikeClickState.value = Unit
    }

    fun onClickName(showUserName: Boolean) {
        if (showUserName) userReviewsState.value = review?.userId
        else companyDetailsState.value = review?.companyId
    }

    fun getPhotos(reviewId: String?) {
        val storageRef = Firebase.storage.reference
        val path = FirebaseHelper.REVIEW_PHOTOS + reviewId
        storageRef.child(path).listAll()
            .addOnSuccessListener { if (it.items.isNotEmpty()) photosState.value = it.items }
            .addOnFailureListener { Timber.e(it) }
    }
}