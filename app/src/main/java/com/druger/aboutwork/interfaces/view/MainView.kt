package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

/**
 * Created by druger on 30.04.2017.
 */

interface MainView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMyReviews(userId: String?)
}
