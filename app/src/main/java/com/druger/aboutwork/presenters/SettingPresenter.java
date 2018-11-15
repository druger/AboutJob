package com.druger.aboutwork.presenters;

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

    private FirebaseUser user;

    public void setupAuth() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getViewState().showEmail(user.getEmail());
            getViewState().showName(user.getDisplayName());
        }
    }

    public void deleteAccount() {
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                            getViewState().showToast(R.string.profile_deleted);
                            getViewState().showMainActivity();
                        } else {
                            getViewState().showToast(R.string.failed_delete_user);
                        }
                    });
        }
    }
}
