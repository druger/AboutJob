package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
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
    fun showAuth()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addReview()
}
