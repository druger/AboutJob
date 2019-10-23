package com.druger.aboutwork.interfaces.view

import moxy.MvpView

/**
 * Created by druger on 16.10.2017.
 */

interface ChangeEmailView : MvpView, NetworkView {

    fun showLoginActivity()
}
