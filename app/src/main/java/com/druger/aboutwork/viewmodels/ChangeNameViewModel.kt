package com.druger.aboutwork.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.druger.aboutwork.db.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import timber.log.Timber

class ChangeNameViewModel : ViewModel() {
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    val successState: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    val errorState: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    val progressState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun changeName(name: String?) {
        name?.let {
            progressState.value = true
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseHelper.changeUserName(name, user.uid)
                        Timber.d("Profile updated")
                        successState.value = Unit
                    } else {
                        Timber.d("Updating failed")
                        errorState.value = Unit
                    }
                    progressState.value = false
                }
        }
    }
}