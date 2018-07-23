package com.druger.aboutwork.interfaces.view;

import android.support.annotation.StringRes;

import com.arellomobile.mvp.MvpView;

/**
 * Created by druger on 11.05.2017.
 */

public interface SettingsView extends MvpView {

    void showLoginActivity();

    void showMainActivity();

    void showToast(@StringRes int resId);

    void showEmail(String email);
}
