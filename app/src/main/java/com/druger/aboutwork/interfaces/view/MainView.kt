package com.druger.aboutwork.interfaces.view

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

/**
 * Created by druger on 30.04.2017.
 */

interface MainView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMyReviews(userId: String?)
}
