package com.druger.aboutwork.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
public class AccountPresenter extends MvpPresenter<AccountView> {
    private static final String TAG = AccountPresenter.class.getSimpleName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;

    public void setupAuth() {
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    getViewState().showLoginActivity();
                }
            }
        };
    }

    public void clickChangeName() {
        getViewState().changeName(user.getUid());
    }

    public void logout() {
        auth.signOut();
    }

    public void addAuthListener() {
        auth.addAuthStateListener(authListener);
    }

    public void removeAuthListener() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void clickOpenSettings() {
        getViewState().openSettings();
    }

    public void clickOpenMyReviews() {
        getViewState().openMyReviews(user.getUid());
    }

    public void changeUserName(String userName, String userId) {
        FirebaseHelper.changeUserName(userName, userId);
    }
}
