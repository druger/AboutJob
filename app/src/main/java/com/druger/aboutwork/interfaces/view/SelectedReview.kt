package com.druger.aboutwork.interfaces.view

import androidx.annotation.StringRes
import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.model.Review
import com.google.firebase.auth.FirebaseUser
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SelectedReview: MvpView {
    fun showChangeDialog(position: Int)
    fun notifyItemRemoved(position: Int, size: Int)
    fun showComments(comments: List<Comment>)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAuth(@StringRes title: Int)
    fun setReview(review: Review?)
    fun onLikeClicked()
    fun onDislikeClicked()
    fun setupComments(user: FirebaseUser?)
    fun clearMessage()
    fun showCompanyDetail(companyId: String?)
}