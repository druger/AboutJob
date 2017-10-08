package com.druger.aboutwork.interfaces.view;

import android.net.Uri;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.google.firebase.storage.StorageReference;

/**
 * Created by druger on 09.05.2017.
 */

public interface AccountView extends MvpView {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void changeName(String userId);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void openSettings();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void openMyReviews(String userId);

    void showLoginActivity();

    void showEmail(String email);

    void checkPermissionReadExternal();

    void startCropImageActivity(Uri imgUri);

    void setupPhoto(Uri imgUri);

    void showPhoto(StorageReference storageRef);

    void showName(String name);
}
