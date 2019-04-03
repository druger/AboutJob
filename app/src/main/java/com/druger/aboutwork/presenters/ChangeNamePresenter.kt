package com.druger.aboutwork.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.druger.aboutwork.interfaces.view.ChangeNameView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@InjectViewState
class ChangeNamePresenter : MvpPresenter<ChangeNameView>() {

    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun changeName(name: String?) {

    }
}