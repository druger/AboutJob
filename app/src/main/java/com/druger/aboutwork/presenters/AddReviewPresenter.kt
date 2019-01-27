package com.druger.aboutwork.presenters

import com.druger.aboutwork.model.MarkCompany
import com.druger.aboutwork.model.Review
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class AddReviewPresenter: ReviewPresenter() {

    var companyId: String? = null

    fun setupReview() {
        val user = FirebaseAuth.getInstance().currentUser
        review = Review(companyId, user?.uid, Calendar.getInstance().timeInMillis)
        mark = MarkCompany(user?.uid, companyId)
        review.markCompany = mark
    }
}