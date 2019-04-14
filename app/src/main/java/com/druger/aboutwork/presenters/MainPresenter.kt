package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.druger.aboutwork.interfaces.view.MainView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by druger on 30.04.2017.
 */

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    fun checkAuthUser() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
    }

    fun onClickMyReviews() = viewState.showMyReviews(user!!.uid)
}
