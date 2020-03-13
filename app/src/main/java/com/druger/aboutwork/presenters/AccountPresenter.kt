package com.druger.aboutwork.presenters

import android.content.Intent
import android.net.Uri
import android.os.Build
import com.druger.aboutwork.App
import com.druger.aboutwork.BuildConfig
import com.druger.aboutwork.R
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.AccountView
import com.druger.aboutwork.model.User
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
class AccountPresenter @Inject constructor() : BasePresenter<AccountView>() {

    @Inject
    lateinit var analytics: Analytics

    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var user: FirebaseUser? = null
    private lateinit var dbReference: DatabaseReference
    private var nameEventListener: ValueEventListener? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        App.appComponent.inject(this)
    }

    fun getUserInfo() {
        dbReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        authListener = FirebaseAuth.AuthStateListener { auth ->
            user = auth.currentUser
            if (user != null) {
                Timber.d("onAuthStateChanged:signed_in:%s", user?.uid)
                viewState.showContent()

                val email = user?.email
                val phone = user?.phoneNumber
                getUserName()

                email?.let { if (it.isNotEmpty()) viewState.showEmail(it) }
                phone?.let { if (it.isNotEmpty()) viewState.showPhone(it) }
            } else {
                viewState.showAuthAccess()
            }
        }
        authListener?.let { auth?.addAuthStateListener(it) }
    }

    private fun getUserName() {
        user?.let { user ->
            for (userInfo in user.providerData) {
                if (userInfo.providerId == "google.com") {
                    getNameFromUsersDb(user.uid, userInfo.displayName)
                } else {
                    viewState.showName(user.displayName)
                }
            }
        }
    }

    private fun getNameFromUsersDb(id: String, displayName: String?) {
        val queryUser = FirebaseHelper.getUser(dbReference, id)
        nameEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.name?.let { viewState.showName(it) } ?: run {
                            displayName?.let {
                                val name = it.split(" ")[0]
                                viewState.showName(name)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.message)
            }
        }
        queryUser.addValueEventListener(nameEventListener as ValueEventListener)
    }

    fun removeAccount() {
        user?.let {
            it.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("User account deleted.")
                        viewState.showToast(R.string.profile_deleted)
                        viewState.showMainActivity()
                        analytics.logEvent(Analytics.REMOVE_ACCOUNT)
                    } else {
                        viewState.showToast(R.string.failed_delete_user)
                    }
                }
        }
    }

    fun writeToDevelopers(email: String) {
        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse(EMAIL_DATA) }

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_TEXT, createSignature())
            selector = emailSelectorIntent
        }
        viewState.sendEmail(emailIntent)
    }

    private fun createSignature(): String {
        return NEW_LINE.plus(UNDERSCORE)
            .plus(UNDERSCORE).plus(NEW_LINE)
            .plus(Build.DEVICE).plus(" ")
            .plus(Build.MODEL).plus(NEW_LINE)
            .plus(APP_VERSION)
            .plus(BuildConfig.VERSION_NAME).plus(NEW_LINE)
            .plus(OS_VERSION)
            .plus(Build.VERSION.RELEASE)
    }

    fun removeAuthListener() {
        authListener?.let { auth?.removeAuthStateListener(it) }
    }

    companion object {
        private const val EMAIL_DATA = "mailto:"
        private const val NEW_LINE = "\n"
        private const val UNDERSCORE = "_"
        private const val OS_VERSION = "OS version: "
        private const val APP_VERSION = "App version: "
    }
}
