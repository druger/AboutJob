package com.druger.aboutwork.interfaces.view

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface ReviewView: MvpView {
    fun showPhotos(uri: Array<Uri?>)
}