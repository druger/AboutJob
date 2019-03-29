package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView
import com.druger.aboutwork.model.Company

/**
 * Created by druger on 01.05.2017.
 */

interface CompaniesView : MvpView, NetworkView {

    fun showCompanies(companies: List<Company>, pages: Int)

    fun showWatchedRecently()

    fun showCompaniesRealm()
}
