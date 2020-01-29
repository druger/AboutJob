package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

/**
 * Created by druger on 01.05.2017.
 */

interface CompanyDetailView : MvpView, NetworkView {

    fun updateAdapter()

    fun showReviews(reviews: List<Review>)

    fun showCompanyDetail(company: CompanyDetail)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAuth()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addReview()
}
