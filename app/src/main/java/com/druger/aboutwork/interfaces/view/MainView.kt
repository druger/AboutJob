package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView

/**
 * Created by druger on 30.04.2017.
 */

interface MainView : MvpView {

    fun showMyReviews(userId: String?)
}
