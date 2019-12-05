package com.druger.aboutwork.presenters

import android.util.Patterns

import com.druger.aboutwork.R
import com.druger.aboutwork.enums.TypeMessage
import com.druger.aboutwork.interfaces.view.ChangeEmailView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import moxy.InjectViewState
import moxy.MvpPresenter
import timber.log.Timber

/**
 * Created by druger on 16.10.2017.
 */
@InjectViewState
class ChangeEmailPresenter : MvpPresenter<ChangeEmailView>() {

    private var user: FirebaseUser? = null

    fun changeEmail(email: String) {
        user = FirebaseAuth.getInstance().currentUser
        viewState.showProgress(true)
        if (user != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            updateEmail(email)
        } else {
            viewState.showMessage(
                R.string.failed_update_email,
                TypeMessage.ERROR)
            viewState.showProgress(false)
        }
    }

    private fun updateEmail(email: String) {
        user?.updateEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("User email address updated.")
                    viewState.showMessage(
                        R.string.updated_email,
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
