package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.interfaces.view.ChangeNameView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import timber.log.Timber

@InjectViewState
class ChangeNamePresenter : BasePresenter<ChangeNameView>() {

    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun changeName(name: String?) {
        viewState.showProgress(true)
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
        user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
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