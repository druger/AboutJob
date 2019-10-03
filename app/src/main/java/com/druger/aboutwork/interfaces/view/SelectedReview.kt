package com.druger.aboutwork.interfaces.view

import android.support.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.model.Review

interface SelectedReview: MvpView {
    fun showChangeDialog(position: Int)
    fun notifyItemRemoved(position: Int, size: Int)
    fun showComments(comments: List<Comment>)
    fun showAuthDialog(@StringRes title: Int)
    fun setReview(review: Review?)
    fun onLikeClicked()
    fun onDislikeClicked()
}