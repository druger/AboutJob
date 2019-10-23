package com.druger.aboutwork.interfaces.view

import moxy.MvpView

interface ChangeNameView : MvpView, NetworkView {
    fun showSuccessMessage()
    fun showErrorMessage()
}