package com.druger.aboutwork.presenters;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.enums.TypeMessage;
import com.druger.aboutwork.interfaces.view.ChangeEmailView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by druger on 16.10.2017.
 */
@InjectViewState
public class ChangeEmailPresenter extends MvpPresenter<ChangeEmailView> {
    private static final String TAG = ChangeEmailPresenter.class.getSimpleName();

    private FirebaseUser user;
    private Context context;

    public void changeEmail(String email, Context context) {
        this.context = context;
        user = FirebaseAuth.getInstance().getCurrentUser();
        getViewState().showProgress(true);
        if (user != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            changeEmail(email);
        } else {
            getViewState().showMessage(
                    context.getString(R.string.failed_update_email),
                    TypeMessage.ERROR);
            getViewState().showProgress(false);
        }
    }

    private void changeEmail(String email) {
        user.updateEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User email address updated.");
                        getViewState().showMessage(
                                context.getString(R.string.updated_email),
                                TypeMessage.SUCCESS);
                        logout();
                    } else {
                        getViewState().showMessage(
                                context.getString(R.string.failed_update_email),
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