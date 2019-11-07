package com.druger.aboutwork.interfaces.view

import android.content.Intent
import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

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
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun sendEmail(emailIntent: Intent)
}