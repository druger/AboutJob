package com.druger.aboutwork.presenters;

import android.app.Activity;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.interfaces.view.LoginView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by druger on 12.05.2017.
 */

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private FirebaseAuth auth;
    private Activity activity;

    public void loginClick(String email, String password) {
        login(email, password);
    }

    private void login(String email, String password) {
        Log.d(TAG, "Login");

        getViewState().showProgress();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    getViewState().hideProgress();

                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail", task.getException());
                        getViewState().onLoginFailed();
                    } else {
                        getViewState().onLoginSuccess();
                    }
                });
    }

    public void setAuth(Activity activity) {
        auth = FirebaseAuth.getInstance();
        this.activity = activity;

        if (auth.getCurrentUser() != null) {
            getViewState().showMainActivity();
        }
    }
}
