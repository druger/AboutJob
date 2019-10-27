package com.druger.aboutwork.presenters;

import android.util.Patterns;

import com.druger.aboutwork.R;
import com.druger.aboutwork.enums.TypeMessage;
import com.druger.aboutwork.interfaces.view.ChangeEmailView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import moxy.InjectViewState;
import moxy.MvpPresenter;
import timber.log.Timber;

/**
 * Created by druger on 16.10.2017.
 */
@InjectViewState
public class ChangeEmailPresenter extends MvpPresenter<ChangeEmailView> {

    private FirebaseUser user;

    public void changeEmail(String email) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        getViewState().showProgress(true);
        if (user != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            updateEmail(email);
        } else {
            getViewState().showMessage(
                    R.string.failed_update_email,
                    TypeMessage.ERROR);
            getViewState().showProgress(false);
        }
    }

    private void updateEmail(String email) {
        user.updateEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.d("User email address updated.");
                        getViewState().showMessage(
                                R.string.updated_email,
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
