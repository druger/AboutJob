package com.druger.aboutwork.interfaces.view

import com.arellomobile.mvp.MvpView

/**
 * Created by druger on 16.10.2017.
 */

interface ChangeEmailView : MvpView, NetworkView {

    fun showLoginActivity()
}
