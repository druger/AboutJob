package com.druger.aboutwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.druger.aboutwork.App;
import com.druger.aboutwork.BuildConfig;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.User;
import com.druger.aboutwork.utils.Analytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;

import javax.inject.Inject;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    Analytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.Companion.getAppComponent().inject(this);
        showAuthUI();
    }

    private void showAuthUI() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()
                        ))
                        .setLogo(R.drawable.ic_logo_full)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG, false)
                        .build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                analytics.logEvent(FirebaseAnalytics.Event.LOGIN);
                if (isNewUser()) {
                    saveNewUser();
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {

                if (idpResponse.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                Timber.e(idpResponse.getError(), "Sign-in error: ");
            }
        }
    }

    private boolean isNewUser() {
        FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
        return metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp();
    }

    private void saveNewUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = firebaseUser.getUid();
        String nameEmail = firebaseUser.getDisplayName();
        String namePhone = "";
        if (nameEmail == null) {
            namePhone = "User_" + id.substring(0, 4);
            setDisplayName(firebaseUser, namePhone);
        }
        String name = nameEmail != null ? nameEmail : namePhone;
        User user = new User(id, name);
        FirebaseHelper.INSTANCE.addUser(user, id);
    }

    private void setDisplayName(FirebaseUser firebaseUser, String namePhone) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(namePhone).build();
        firebaseUser.updateProfile(profileUpdates);
    }


    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        RefWatcher refWatcher = App.Companion.getRefWatcher(this);
//        refWatcher.watch(this);
//    }
}
