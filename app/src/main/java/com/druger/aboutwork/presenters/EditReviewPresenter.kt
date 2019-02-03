package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import javax.inject.Inject

@InjectViewState
class EditReviewPresenter @Inject constructor(): ReviewPresenter() {

    private lateinit var review: Review
    private lateinit var mark: MarkCompany

    fun setupRating(review: Review) {
         this.review = review
         mark = review.markCompany
         review.markCompany = mark
    }

    override fun doneClick() {
        updateReview()
    }

    private fun updateReview() {
        if (isCorrectStatus() && isCorrectReview(review)) {
            review.status = status
            FirebaseHelper.updateReview(review)
            viewState.successfulEditing()
        } else {
            viewState.showErrorEditing()
        }
    }

    private fun isCorrectStatus(): Boolean {
        return (status == WORKING_STATUS || status == WORKED_STATUS) && mark.averageMark != 0F
                || status == INTERVIEW_STATUS && mark.averageMark == 0F
    }

    fun setSalary(rating: Float) {
        mark.salary = rating
    }

    fun setChief(rating: Float) {
        mark.chief = rating
    }

    fun setWorkplace(rating: Float) {
        mark.workplace = rating
    }

    fun setCareer(rating: Float) {
        mark.career = rating
    }

    fun setCollective(rating: Float) {
        mark.collective = rating
    }

    fun setSocialPackage(rating: Float) {
        mark.socialPackage = rating
    }
}