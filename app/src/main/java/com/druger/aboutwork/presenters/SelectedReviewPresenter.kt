package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.db.FirebaseHelper.getComments
import com.druger.aboutwork.interfaces.view.SelectedReview
import com.druger.aboutwork.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

@InjectViewState
class SelectedReviewPresenter : BasePresenter<SelectedReview>(), ValueEventListener {

    private var user: FirebaseUser? = null
    private var dbReference = FirebaseDatabase.getInstance().reference

    private var comments: List<Comment> = emptyList()
    lateinit var comment: Comment

    override fun attachView(view: SelectedReview?) {
        super.attachView(view)
        user = FirebaseAuth.getInstance().currentUser
    }

    fun addComment(message: String, reviewId: String) {
        val calendar = Calendar.getInstance()
        val comment = Comment(message, calendar.timeInMillis)
        comment.userId = user?.uid
        comment.userName = user?.displayName
        comment.reviewId = reviewId
        FirebaseHelper.addComment(comment)
    }

    fun updateComment(message: String) {
        FirebaseHelper.updateComment(comment.id, message)
    }

    fun onLongClick(position: Int): Boolean {
        comment = comments[position]
        if (comment.userId == user?.uid) {
            viewState.showChangeDialog(position)
            return true
        }
        return false
    }

    fun deleteComment(position: Int) {
        FirebaseHelper.deleteComment(comment.id)
        comments = comments.toMutableList().apply { removeAt(position) }
        viewState.notifyItemRemoved(position, comments.size)
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
        viewState.showComments(comments)
    }

    override fun onCancelled(p0: DatabaseError) {}

    fun removeListeners() {
        dbReference.removeEventListener(this)
    }
}