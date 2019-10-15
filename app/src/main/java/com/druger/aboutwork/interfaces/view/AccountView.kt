package com.druger.aboutwork.interfaces.view

import android.content.Intent
import android.support.annotation.StringRes
import com.arellomobile.mvp.MvpView

/**
 * Created by druger on 09.05.2017.
 */

interface AccountView : MvpView {

    fun showName(name: String?)

    fun showMainActivity()

    fun showToast(@StringRes resId: Int)

    fun showEmail(email: String?)
    fun showAuthAccess()
    fun showPhone(phone: String?)
    fun sendEmail(emailIntent: Intent)
}