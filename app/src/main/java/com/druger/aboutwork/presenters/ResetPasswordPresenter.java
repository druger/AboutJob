package com.druger.aboutwork.presenters;

import android.text.TextUtils;
import android.util.Patterns;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.ResetPasswordView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by druger on 13.05.2017.
 */

@InjectViewState
public class ResetPasswordPresenter extends MvpPresenter<ResetPasswordView> {

    private FirebaseAuth auth;

    public void setAuth() {
        auth = FirebaseAuth.getInstance();
    }

    public void resetPassClick(String email) {
        resetPassword(email);
    }

    private void resetPassword(String email) {
        getViewState().showProgress();

        if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            getViewState().showToast(R.string.error_email);
            getViewState().hideProgress();
        } else {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            getViewState().showToast(R.string.sent_reset_pass);
                            getViewState().doResetPass();
                        } else {
                            getViewState().showToast(R.string.failed_reset_pass);
                        }
                        getViewState().hideProgress();
                    });
        }
    }
}
