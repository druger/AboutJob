package com.druger.aboutwork.presenters

import android.content.Intent
import android.net.Uri
import android.os.Build
import com.druger.aboutwork.App
import com.druger.aboutwork.BuildConfig
import com.druger.aboutwork.R
import com.druger.aboutwork.db.RealmHelper
import com.druger.aboutwork.interfaces.view.AccountView
import com.druger.aboutwork.utils.Analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
class AccountPresenter @Inject constructor(
    realmHelper: RealmHelper
) : BasePresenter<AccountView>() {

    @Inject
    lateinit var analytics: Analytics

    private var user: FirebaseUser? = null

    init {
        this.realmHelper = realmHelper
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        App.appComponent.inject(this)
    }

    fun getUserInfo() {
        user = FirebaseAuth.getInstance().currentUser
        user?.let {
            Timber.d("onAuthStateChanged:signed_in:%s", it.uid)

            val email = it.email
            val name = it.displayName
            val phone = it.phoneNumber

            viewState.showEmail(email)
            viewState.showName(name)
            viewState.showPhone(phone)

        } ?: viewState.showAuthAccess()
    }

    fun logout() {
        realmHelper.deleteAllData()
        getUserInfo()
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

    companion object {
        private const val EMAIL_DATA = "mailto:"
        private const val NEW_LINE = "\n"
        private const val UNDERSCORE = "_"
        private const val OS_VERSION = "OS version: "
        private const val APP_VERSION = "App version: "
    }
}
