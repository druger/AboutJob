package com.druger.aboutwork.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.RealmHelper;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
public class AccountPresenter extends BasePresenter<AccountView> {

    private FirebaseUser user;
    private DatabaseReference dbReference;

    @Inject
    public AccountPresenter(RealmHelper realmHelper) {
        this.realmHelper = realmHelper;
    }

    public void getUserInfo() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            dbReference = FirebaseDatabase.getInstance().getReference();
            getViewState().showEmail(user.getEmail());
            getViewState().showName(user.getDisplayName());
        } else getViewState().showAuthAccess();
    }

    public void logout() {
        realmHelper.deleteAllData();
        getUserInfo();
    }

    public void removeAccount() {
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
