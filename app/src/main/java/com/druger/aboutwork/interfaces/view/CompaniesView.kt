package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.model.Review
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

/**
 * Created by druger on 01.05.2017.
 */

@StateStrategyType(OneExecutionStateStrategy::class)
interface CompaniesView : MvpView, NetworkView {

    fun showReviews(reviews: List<Review>)
    fun showEmptyReviews()
    fun updateAdapter()
}
