package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.druger.aboutwork.App
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.rest.RestApi
import javax.inject.Inject

@InjectViewState
class EditReviewPresenter @Inject constructor(): ReviewPresenter() {

    fun setupRating(review: Review?) {
         this.review = review
         mark = review?.markCompany
         review?.markCompany = mark
    }

    override fun doneClick() {
        updateReview()
    }

    private fun updateReview() {
        if (isCorrectStatus && isCorrectReview(review)) {
            review.status = status
            FirebaseHelper.updateReview(review)
            viewState.successfulEditing()
        } else {
            viewState.showErrorEditing()
        }
    }
}