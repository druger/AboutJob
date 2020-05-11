package com.druger.aboutwork.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class LoginActivity : AppCompatActivity(), KoinComponent {

    private val analytics: Analytics by inject()

    private var nextScreen: String? = null
    private var companyId: String? = null
    private var reviewId: String? = null
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showAuthUI()
        getExtras()
    }

    private fun getExtras() {
        nextScreen = intent.getStringExtra(MainActivity.NEXT_SCREEN)
        companyId = intent.getStringExtra(MainActivity.COMPANY_ID)
        reviewId = intent.getStringExtra(MainActivity.REVIEW_ID)
        message = intent.getStringExtra(MainActivity.MESSAGE)
    }

    private fun showAuthUI() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.PhoneBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.AnonymousBuilder().build()
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
                sendAnalytics(idpResponse)
                if (isNewUser()) saveNewUser()
                startMainActivity()
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

    private fun sendAnalytics(idpResponse: IdpResponse?) {
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN)
        val type = idpResponse?.providerType
        type?.let { analytics.logEvent(Analytics.SIGN_IN_TYPE, it) }
    }

    private fun startMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(MainActivity.NEXT_SCREEN, nextScreen)
            putExtra(MainActivity.COMPANY_ID, companyId)
            putExtra(MainActivity.REVIEW_ID, reviewId)
            putExtra(MainActivity.MESSAGE, message)
        }
        startActivity(intent)
    }

    private fun isNewUser(): Boolean {
        val metadata = FirebaseAuth.getInstance().currentUser?.metadata
        val diff = metadata?.lastSignInTimestamp?.minus(metadata.creationTimestamp)
        return diff == 0L || diff == 1L
    }

    private fun saveNewUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val id = firebaseUser?.uid
        val name = "User_" + id?.substring(0, 4)
        val user = id?.let { User(it, name) }
        firebaseUser?.let { setDisplayName(it, name) }
        user?.let { FirebaseHelper.addUser(it, id) }
    }

    private fun setDisplayName(firebaseUser: FirebaseUser, name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()
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
