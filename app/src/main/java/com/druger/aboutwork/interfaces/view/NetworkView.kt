package com.druger.aboutwork.interfaces.view

import androidx.annotation.StringRes
import com.druger.aboutwork.enums.TypeMessage

/**
 * Created by druger on 06.08.2017.
 */

interface NetworkView {

    fun showProgress(show: Boolean)

    fun showMessage(@StringRes message: Int, typeMessage: TypeMessage)

    fun showMessage(message: String)

    fun showErrorScreen(show: Boolean)
}
