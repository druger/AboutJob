package com.druger.aboutwork.presenters;

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

    private FirebaseUser user;

    public void changePassword(String password) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        getViewState().showProgress(true);
        if (user != null && password!= null && !password.isEmpty()) {
            updatePassword(password);
        } else {
            getViewState().showMessage(
                    R.string.failed_update_pass,
                    TypeMessage.ERROR);
            getViewState().showProgress(false);
        }
    }

    private void updatePassword(String password) {
        user.updatePassword(password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.d("User password updated.");
                        getViewState().showMessage(
                                R.string.success_update_pass,
                                TypeMessage.SUCCESS);
                        logout();
                    } else {
                        Timber.e(task.getException());
                        String error = task.getException().getLocalizedMessage();
                        if (error != null) {
                            getViewState().showMessage(error);
                        } else {
                            getViewState().showMessage(R.string.failed_update_email, TypeMessage.ERROR);
                        }
                    }
                    getViewState().showProgress(false);
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        getViewState().showLoginActivity();
    }
}
