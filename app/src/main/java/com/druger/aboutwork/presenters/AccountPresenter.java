package com.druger.aboutwork.presenters;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.App;
import com.druger.aboutwork.BuildConfig;
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

    private static final String EMAIL_DATA = "mailto:";
    private static final String NEW_LINE = "\n";
    private static final String UNDERSCORE = "_";
    private static final String OS_VERSION = "OS version: ";
    private static final String APP_VERSION = "App version: ";

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

            String email = user.getEmail();
            String name = user.getDisplayName();
            String phone = user.getPhoneNumber();

            if (email != null) getViewState().showEmail(email);
            if (name != null) getViewState().showName(name);
            if (phone != null) getViewState().showPhone(phone);

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

    public void writeToDevelopers(String email) {
        Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
        emailSelectorIntent.setData(Uri.parse(EMAIL_DATA));

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ email });
        emailIntent.putExtra(Intent.EXTRA_TEXT, createSignature());

        emailIntent.setSelector(emailSelectorIntent);

        getViewState().sendEmail(emailIntent);
    }

    private String createSignature() {
        return NEW_LINE.concat(UNDERSCORE)
                .concat(UNDERSCORE).concat(NEW_LINE)
                .concat(Build.DEVICE).concat(" ")
                .concat(Build.MODEL).concat(NEW_LINE)
                .concat(APP_VERSION)
                .concat(BuildConfig.VERSION_NAME).concat(NEW_LINE)
                .concat(OS_VERSION)
                .concat(Build.VERSION.RELEASE);
    }
}
