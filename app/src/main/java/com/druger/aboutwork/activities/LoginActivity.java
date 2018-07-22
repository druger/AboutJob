package com.druger.aboutwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.druger.aboutwork.App;
import com.druger.aboutwork.BuildConfig;
import com.druger.aboutwork.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.squareup.leakcanary.RefWatcher;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                        .build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                finish();
            } else {
                if (idpResponse == null) {
                    Toast.makeText(this,R.string.sign_in_cancelled, Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (idpResponse.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    finish();
                }

                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Sign-in error: ", idpResponse.getError());
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.Companion.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
