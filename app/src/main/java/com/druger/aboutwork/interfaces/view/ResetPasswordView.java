package com.druger.aboutwork.interfaces.view;

import android.support.annotation.StringRes;

import com.arellomobile.mvp.MvpView;

/**
 * Created by druger on 13.05.2017.
 */

public interface ResetPasswordView extends MvpView {

    void showToast(@StringRes int resId);

    void hideProgress();

    void showProgress();

    void doResetPass();
}
