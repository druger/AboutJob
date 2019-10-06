package com.druger.aboutwork.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.RealmHelper;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.druger.aboutwork.utils.Analytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
public class AccountPresenter extends BasePresenter<AccountView> {

    @Inject
    Analytics analytics;

    private FirebaseUser user;

    @Inject
    public AccountPresenter(RealmHelper realmHelper) {
        this.realmHelper = realmHelper;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        App.Companion.getAppComponent().inject(this);
    }

    public void getUserInfo() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Timber.d("onAuthStateChanged:signed_in:%s", user.getUid());

            getViewState().showEmail(user.getEmail());
            getViewState().showName(user.getDisplayName());
            getViewState().showPhone(user.getPhoneNumber());
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
                            Timber.d("User account deleted.");
                            getViewState().showToast(R.string.profile_deleted);
                            getViewState().showMainActivity();
                            analytics.logEvent(Analytics.REMOVE_ACCOUNT);
                        } else {
                            getViewState().showToast(R.string.failed_delete_user);
                        }
                    });
        }
    }
}
