package com.druger.aboutwork.interfaces.view

import android.net.Uri
import android.support.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.google.firebase.storage.StorageReference

/**
 * Created by druger on 09.05.2017.
 */

interface AccountView : MvpView {

    fun setupPhoto(imgUri: Uri)

    fun showPhoto(storageRef: StorageReference)

    fun showHeaderName(name: String)

    fun showName(name: String)

    fun showMainActivity()

    fun showToast(@StringRes resId: Int)

    fun showEmail(email: String)
    fun showAuthAccess()
}
