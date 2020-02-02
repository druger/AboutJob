package com.druger.aboutwork.presenters

import com.druger.aboutwork.App
import com.druger.aboutwork.interfaces.view.FilterView
import javax.inject.Inject

class FilterPresenter @Inject constructor() : BasePresenter<FilterView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        App.appComponent.inject(this)
    }

    fun filterAtRating() {

    }

    fun filterAtSalary() {

    }

    fun filterAtChief() {

    }

    fun filterAtWorkplace() {

    }

    fun filterAtCareer() {

    }

    fun filterAtCollective() {

    }

    fun filterAtBenefits() {

    }

    fun filterAtPopularity() {

    }
}