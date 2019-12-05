package com.druger.aboutwork.presenters

import com.druger.aboutwork.R
import com.druger.aboutwork.enums.TypeMessage
import com.druger.aboutwork.interfaces.view.ChangePasswordView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import moxy.InjectViewState
import moxy.MvpPresenter
import timber.log.Timber

/**
 * Created by druger on 22.10.2017.
 */

@InjectViewState
class ChangePasswordPresenter : MvpPresenter<ChangePasswordView>() {

    private var user: FirebaseUser? = null

    fun changePassword(password: String?) {
        user = FirebaseAuth.getInstance().currentUser
        viewState.showProgress(true)
        if (user != null && password != null && password.isNotEmpty()) {
            updatePassword(password)
        } else {
            viewState.showMessage(
                R.string.failed_update_pass,
                TypeMessage.ERROR)
            viewState.showProgress(false)
        }
    }

    private fun updatePassword(password: String) {
        user?.updatePassword(password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("User password updated.")
                    viewState.showMessage(
                        R.string.success_update_pass,
                        TypeMessage.SUCCESS)
                    logout()
                } else {
                    Timber.e(task.exception)
                    val error = task.exception?.localizedMessage
                    error?.let {
                        viewState.showMessage(error)
                    } ?: viewState.showMessage(R.string.failed_update_email, TypeMessage.ERROR)
                }
                viewState.showProgress(false)
            }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        viewState.showLoginActivity()
    }
}
