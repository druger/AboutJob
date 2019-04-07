package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.enums.TypeMessage

/**
 * Created by druger on 06.08.2017.
 */

interface NetworkView {

    fun showProgress(show: Boolean)

    fun showMessage(message: String, typeMessage: TypeMessage)

    fun showErrorScreen(show: Boolean)
}
