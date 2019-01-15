package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView
import com.druger.aboutwork.model.Comment

interface SelectedReview: MvpView {
    fun showChangeDialog(position: Int)
    fun notifyItemRemoved(position: Int, size: Int)
    fun showComments(comments: List<Comment>)
}