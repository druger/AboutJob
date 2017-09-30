package com.druger.aboutwork.presenters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.SettingsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by druger on 11.05.2017.
 */

@InjectViewState
public class SettingPresenter extends MvpPresenter<SettingsView> {

    private static final String TAG = SettingPresenter.class.getSimpleName();
    private static final int PASSWORD_LENGTH = 6;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;

    public void setupAuth() {
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = firebaseAuth -> {
            if (user == null) {
                getViewState().showLoginActivity();
            }
        };
    }

    public void addAuthStateListener() {
        auth.addAuthStateListener(authListener);
    }

    public void removeAuthStateListener() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void checkEmail(String email, Context context) {
        if (user != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && !TextUtils.isEmpty(email)) {
            changeEmail(email);
        } else if (TextUtils.isEmpty(email)) {
            getViewState().showError(context.getString(R.string.valid_email));
        }
    }

    private void changeEmail(String newEmail) {
        user.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User email address updated.");
                        getViewState().showToast(R.string.updated_email);
                        logout();
                    } else {
                        getViewState().showToast(R.string.failed_update_email);
                    }
                    getViewState().hideProgress();
                });
    }


    public void checkPassword(String password, Context context) {
        if (user != null && !TextUtils.isEmpty(password)) {
            changePassword(password, context);
        } else if (TextUtils.isEmpty(password)) {
            getViewState().showError(context.getString(R.string.enter_pass));
        }
    }

    private void changePassword(String password, Context context) {
        if (password.length() < PASSWORD_LENGTH) {
            getViewState().showError(context.getString(R.string.pass_error));
        } else {
            user.updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                            getViewState().showToast(R.string.success_update_pass);
                            logout();
                        } else {
                            getViewState().showToast(R.string.failed_update_pass);
                        }
                        getViewState().hideProgress();
                    });
        }
    }

    private void logout() {
        auth.signOut();
        getViewState().showLoginActivity();
    }

    public void deleteAccount() {
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                            getViewState().showToast(R.string.profile_deleted);
                            getViewState().showSignupActivity();
                        } else {
                            getViewState().showToast(R.string.failed_delete_user);
                        }
                        getViewState().hideProgress();
                    });
        }
    }
}
