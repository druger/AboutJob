package com.druger.aboutwork.presenters

import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.ChangeNameView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChangeNamePresenter @Inject constructor() : BasePresenter<ChangeNameView>() {

    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun changeName(name: String?) {
        name?.let {
            viewState.showProgress(true)
            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build()
            user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseHelper.changeUserName(name, user.uid)
                            Timber.d("Profile updated")
                            viewState.showSuccessMessage()
                        } else {
                            Timber.d("Updating failed")
                            viewState.showSuccessMessage()
                        }
                        viewState.showProgress(false)
                    }
        }
    }
}