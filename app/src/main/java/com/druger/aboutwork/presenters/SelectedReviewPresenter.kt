package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.db.FirebaseHelper.getComments
import com.druger.aboutwork.interfaces.view.SelectedReview
import com.druger.aboutwork.model.Comment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

@InjectViewState
class SelectedReviewPresenter : BasePresenter<SelectedReview>(), ValueEventListener {

    private val user: FirebaseUser? = null
    private var dbReference: DatabaseReference? = null

    private lateinit var comments: MutableList<Comment>
    var comment: Comment? = null

    fun addComment(message: String, reviewId: String) {
        val calendar = Calendar.getInstance()
        val comment = Comment(message, calendar.timeInMillis)
        if (user != null) {
            comment.userId = user.uid
        }
        comment.reviewId = reviewId
        FirebaseHelper.addComment(comment)
    }

    fun updateComment(message: String) {
        FirebaseHelper.updateComment(comment?.id, message)
    }

    fun onLongClick(position: Int): Boolean {
        comment = comments[position]
        if (comment?.userId == user?.uid) {
            viewState.showChangeDialog(position)
            return true
        }
        return false
    }

    fun deleteComment(position: Int) {
        FirebaseHelper.deleteComment(comment?.id)
        comments.removeAt(position)
        viewState.notifyItemRemoved(position, comments.size)
    }

    fun retrieveComments(reviewId: String) {
        dbReference = FirebaseDatabase.getInstance().reference
        val commentsQuery = getComments(dbReference, reviewId)
        commentsQuery.addValueEventListener(this)
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        comments.clear()
        for (snapshot in dataSnapshot.children) {
            val comment = snapshot.getValue(Comment::class.java)
            comment!!.id = snapshot.key
            comments.add(comment)
        }
        viewState.showComments(comments)
    }

    override fun onCancelled(p0: DatabaseError) {}

    fun removeListeners() {
        dbReference?.removeEventListener(this)
    }
}