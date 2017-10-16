package com.druger.aboutwork.presenters;

import android.content.Context;
import android.util.Log;

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
            } else {
                getViewState().showEmail(user.getEmail());
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

    private void changePassword(String password, Context context) {
        if (password.length() < PASSWORD_LENGTH) {
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
                    });
        }
    }
}
