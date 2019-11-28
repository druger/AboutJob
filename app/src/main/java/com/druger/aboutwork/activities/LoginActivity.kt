package com.druger.aboutwork.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.druger.aboutwork.App
import com.druger.aboutwork.BuildConfig
import com.druger.aboutwork.R
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.User
import com.druger.aboutwork.utils.Analytics
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import timber.log.Timber
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    internal lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        showAuthUI()
    }

    private fun showAuthUI() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.PhoneBuilder().build(),
                    AuthUI.IdpConfig.EmailBuilder().build()
                ))
                .setLogo(R.drawable.ic_logo)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, false)
                .build(), RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val idpResponse = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                analytics.logEvent(FirebaseAnalytics.Event.LOGIN)
                if (isNewUser()) {
                    saveNewUser()
                }
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {

                if (idpResponse?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
                    return
                }

                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show()
                Timber.e(idpResponse?.error, "Sign-in error: ")
            }
        }
    }

    private fun isNewUser(): Boolean {
        val metadata = FirebaseAuth.getInstance().currentUser?.metadata
        return metadata?.creationTimestamp == metadata?.lastSignInTimestamp
    }

    private fun saveNewUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val id = firebaseUser?.uid
        val nameEmail = firebaseUser?.displayName
        var namePhone = ""
        if (nameEmail == null) {
            namePhone = "User_" + id?.substring(0, 4)
            firebaseUser?.let { setDisplayName(it, namePhone) }
        }
        val name = nameEmail ?: namePhone
        val user = id?.let { User(it, name) }
        user?.let { FirebaseHelper.addUser(it, id) }
    }

    private fun setDisplayName(firebaseUser: FirebaseUser, namePhone: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(namePhone).build()
        firebaseUser.updateProfile(profileUpdates)
    }

    companion object {
        private const val RC_SIGN_IN = 1
    }


    //    @Override
    //    protected void onDestroy() {
    //        super.onDestroy();
    //        RefWatcher refWatcher = App.Companion.getRefWatcher(this);
    //        refWatcher.watch(this);
    //    }
}