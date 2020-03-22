package com.druger.aboutwork.interfaces.view

import androidx.annotation.DrawableRes
import com.druger.aboutwork.model.CompanyDetail
import com.druger.aboutwork.model.Review
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

/**
 * Created by druger on 01.05.2017.
 */

@StateStrategyType(OneExecutionStateStrategy::class)
interface CompanyDetailView : MvpView, NetworkView {

    fun updateAdapter()

    fun showReviews(reviews: List<Review>, isFilter: Boolean = false)

    fun showCompanyDetail(company: CompanyDetail)

    fun showAuth()

    fun addReview()

    fun showFilterDialog(position: String, city: String)

    fun setFilterIcon(@DrawableRes icFilter: Int)

    fun showEmptyReviews(isFilter: Boolean = false)
}
