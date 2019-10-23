package com.druger.aboutwork.presenters;

import android.content.Context;

import com.druger.aboutwork.R;
import com.druger.aboutwork.enums.TypeMessage;
import com.druger.aboutwork.interfaces.view.ChangePasswordView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import moxy.InjectViewState;
import moxy.MvpPresenter;
import timber.log.Timber;

/**
 * Created by druger on 22.10.2017.
 */

@InjectViewState
public class ChangePasswordPresenter extends MvpPresenter<ChangePasswordView> {
    private static final String TAG = ChangeEmailPresenter.class.getSimpleName();
    private static final int PASSWORD_LENGTH = 6;

    private FirebaseUser user;
    private Context context;

    public void changePassword(String password, Context context) {
        this.context = context;
        user = FirebaseAuth.getInstance().getCurrentUser();
        getViewState().showProgress(true);
        if (user != null && password.length() >= PASSWORD_LENGTH) {
            changePassword(password);
        } else {
            getViewState().showMessage(
                    context.getString(R.string.failed_update_pass),
                    TypeMessage.ERROR);
            getViewState().showProgress(false);
        }
    }

    private void changePassword(String password) {
        user.updatePassword(password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.d("User password updated.");
                        getViewState().showMessage(
                                context.getString(R.string.success_update_pass),
                                TypeMessage.SUCCESS);
                        logout();
                    } else {
                        getViewState().showMessage(
                                context.getString(R.string.failed_update_pass),
                                TypeMessage.ERROR);
                    }
                    getViewState().showProgress(false);
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        getViewState().showLoginActivity();
    }
}
