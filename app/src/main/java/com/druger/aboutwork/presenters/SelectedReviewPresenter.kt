package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.App
import com.druger.aboutwork.R
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.db.FirebaseHelper.getComments
import com.druger.aboutwork.interfaces.view.SelectedReview
import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import javax.inject.Inject

@InjectViewState
class SelectedReviewPresenter : BasePresenter<SelectedReview>(), ValueEventListener {

    @Inject
    lateinit var analytics: Analytics

    var user: FirebaseUser? = null
    private var dbReference = FirebaseDatabase.getInstance().reference
    private lateinit var reviewListener: ValueEventListener
    private lateinit var companyListener: ValueEventListener

    private var comments: List<Comment> = emptyList()
    lateinit var comment: Comment
    private var review: Review? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        App.appComponent.inject(this)
        user = FirebaseAuth.getInstance().currentUser
    }

    override fun attachView(view: SelectedReview?) {
        super.attachView(view)
        viewState.setupComments(user)
    }

    fun addComment(message: String, reviewId: String) {
        if (user != null) {
            val calendar = Calendar.getInstance()
            val comment = Comment(message, calendar.timeInMillis)
            comment.userId = user?.uid
            comment.userName = user?.displayName
            comment.reviewId = reviewId
            FirebaseHelper.addComment(comment)
            analytics.logEvent(Analytics.ADD_COMMENT)
        } else {
            viewState.showAuthDialog(R.string.comment_login)
        }
    }

    fun updateComment(message: String) {
        FirebaseHelper.updateComment(comment.id, message)
        analytics.logEvent(Analytics.UPDATE_COMMENT)
    }

    fun onLongClick(position: Int): Boolean {
        comment = comments[position]
        if (comment.userId == user?.uid) {
            viewState.showChangeDialog(position)
            analytics.logEvent(Analytics.LONG_CLICK_MY_COMMENT)
            return true
        }
        return false
    }

    fun deleteComment(position: Int) {
        FirebaseHelper.deleteComment(comment.id)
        comments = comments.toMutableList().apply { removeAt(position) }
        viewState.notifyItemRemoved(position, comments.size)
        analytics.logEvent(Analytics.DELETE_COMMENT)
    }

    fun retrieveComments(reviewId: String) {
        val commentsQuery = getComments(dbReference, reviewId)
        commentsQuery.addValueEventListener(this)
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        comments = comments.toMutableList().apply { clear() }
        for (snapshot in dataSnapshot.children) {
            val comment = snapshot.getValue(Comment::class.java)
            comment?.id = snapshot.key.toString()
            comments = comments.toMutableList().apply { comment?.let { add(it) } }
        }
        viewState.showComments(comments.reversed())
    }

    override fun onCancelled(p0: DatabaseError) {}

    fun removeListeners() {
        dbReference.removeEventListener(this)
        reviewListener.let { dbReference.removeEventListener(reviewListener) }
    }

    fun getReview(reviewKey: String) {
        val queryReview = FirebaseHelper.getReview(dbReference, reviewKey)
        reviewListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                review = snapshot.getValue(Review::class.java)
                review?.firebaseKey = snapshot.key
                getCompany()
            }
        }
        queryReview.addValueEventListener(reviewListener)
    }

    private fun getCompany() {
        val queryCompany = review?.let { review ->
            review.companyId?.let { id ->
            FirebaseHelper.getCompany(dbReference, id)
        } }
        companyListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val company = snapshot.getValue(Company::class.java)
                review?.name = company?.name
                viewState.setReview(review)
            }
        }
        queryCompany?.addValueEventListener(companyListener)
    }

    fun clickLike() {
        if (user == null) viewState.showAuthDialog(R.string.like_login)
        else viewState.onLikeClicked()
    }

    fun clickDislike() {
        if (user == null) viewState.showAuthDialog(R.string.dislike_login)
        else viewState.onDislikeClicked()
    }
}