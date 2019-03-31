package com.druger.aboutwork.interfaces.view

import android.net.Uri

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.firebase.storage.StorageReference

/**
 * Created by druger on 09.05.2017.
 */

interface AccountView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openSettings()

    fun checkPermissionReadExternal()

    fun startCropImageActivity(imgUri: Uri)

    fun setupPhoto(imgUri: Uri)

    fun showPhoto(storageRef: StorageReference)

    fun showName(name: String)
}
