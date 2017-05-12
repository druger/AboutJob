package com.druger.aboutwork.presenters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.SignupView;
import com.druger.aboutwork.model.User;
import com.druger.aboutwork.utils.SharedPreferencesHelper;
import com.druger.aboutwork.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by druger on 13.05.2017.
 */

@InjectViewState
public class SignupPresenter extends MvpPresenter<SignupView> {
    private static final String TAG = SignupPresenter.class.getSimpleName();

    private FirebaseAuth auth;
    private Activity activity;

    public void setAuth(Activity activity) {
        this.activity = activity;
        auth = FirebaseAuth.getInstance();
    }

    public void signupClick(String email, String password) {
        signup(email, password);
    }

    private void signup(String email, String password) {
        if (!getViewState().validate(email, password)) {
            getViewState().onSignupFailed();
            return;
        }

        getViewState().showProgress();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        getViewState().hideProgress();

                        if (!task.isSuccessful()) {
                            getViewState().onSignupFailed();
                        } else {
                            saveNewUser(task.getResult().getUser());
                            getViewState().onSignupSuccess();
                        }

                    }
                });
    }

    private void saveNewUser(FirebaseUser firebaseUser) {
        String id = firebaseUser.getUid();
        String name = Utils.getNameByEmail(firebaseUser.getEmail());
        User user = new User(id, name);
        FirebaseHelper.addUser(user, firebaseUser.getUid());
        SharedPreferencesHelper.saveUserName(name, activity);
    }
}
