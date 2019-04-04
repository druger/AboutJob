package com.druger.aboutwork.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.druger.aboutwork.interfaces.view.ChangeNameView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

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
                        Log.d(TAG, "Profile updated")
                        viewState.showSuccessMessage()
                    } else {
                        Log.d(TAG, "Updating failed")
                        viewState.showSuccessMessage()
                    }
                    viewState.showProgress(false)
                }
    }
}