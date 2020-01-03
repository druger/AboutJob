package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.model.Company
import moxy.MvpView

/**
 * Created by druger on 01.05.2017.
 */

interface SearchView : MvpView, NetworkView {

    fun showCompanies(companies: List<Company>, pages: Int)
}
