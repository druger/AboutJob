package com.druger.aboutwork.interfaces.view

import moxy.MvpView

/**
 * Created by druger on 01.05.2017.
 */

interface CompaniesView : MvpView, NetworkView {

    fun showWatchedRecently()

    fun showCompaniesRealm()
}
