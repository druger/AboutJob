package com.druger.aboutwork.interfaces.view

import moxy.MvpView

/**
 * Created by druger on 22.10.2017.
 */

interface ChangePasswordView : MvpView, NetworkView {

    fun showLoginActivity()

}
