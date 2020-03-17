package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.model.Review
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

/**
 * Created by druger on 09.05.2017.
 */
@StateStrategyType(OneExecutionStateStrategy::class)
interface MyReviewsView : MvpView, NetworkView {

    fun showReviews(reviews: List<Review>)
    fun updateAdapter()
}
