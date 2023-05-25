package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private var user: FirebaseUser? = null

    val userId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

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
        userId.value = user?.uid
    }
}