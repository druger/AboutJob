package com.druger.aboutwork.presenters

import com.druger.aboutwork.interfaces.view.MainView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

/**
 * Created by druger on 30.04.2017.
 */

@InjectViewState
class MainPresenter @Inject constructor(): MvpPresenter<MainView>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private var user: FirebaseUser? = null

    fun checkAuthUser() {
        auth = FirebaseAuth.getInstance()
        initAuthListener()
        auth.addAuthStateListener(authListener)
        user = auth.currentUser
    }

    private fun initAuthListener() {
        authListener = FirebaseAuth.AuthStateListener {
            user = it.currentUser
        }
    }

    fun removeAuthListener() = auth.removeAuthStateListener(authListener)

    fun isUserLoggedIn(): Boolean {
        return user != null
    }

    fun onClickMyReviews() {
        val userId = user?.uid
        viewState.showMyReviews(userId)
    }
}
