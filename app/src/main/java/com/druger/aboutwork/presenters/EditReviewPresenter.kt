package com.druger.aboutwork.presenters

import com.druger.aboutwork.model.Review

class EditReviewPresenter: ReviewPresenter() {

     fun setupRating(review: Review?) {
         this.review = review
         mark = review?.markCompany
         review?.markCompany = mark
    }
}