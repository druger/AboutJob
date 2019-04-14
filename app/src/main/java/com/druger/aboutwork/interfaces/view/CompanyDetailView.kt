package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review

/**
 * Created by druger on 01.05.2017.
 */

interface CompanyDetailView : MvpView, NetworkView {

    fun updateAdapter()

    fun showReviews(reviews: List<Review>)

    fun showCompanyDetail(company: CompanyDetail)

    fun showProgressReview()

    fun hideProgressReview()
    fun showLoginActivity()
    fun addReview()
}
