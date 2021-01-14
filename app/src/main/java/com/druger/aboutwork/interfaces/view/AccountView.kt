package com.druger.aboutwork.interfaces.view

import android.content.Intent
import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

/**
 * Created by druger on 09.05.2017.
 */

@StateStrategyType(OneExecutionStateStrategy::class)
interface AccountView : MvpView {

    fun showName(name: String?)

    fun showMainActivity()

    fun showToast(@StringRes resId: Int)

    fun showEmail(email: String)
    fun showNotAuthSetting()
    fun showPhone(phone: String)
    fun sendEmail(emailIntent: Intent)
    fun showAuthSetting()
}