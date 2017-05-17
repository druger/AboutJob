package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;

/**
 * Created by druger on 12.05.2017.
 */

public interface LoginView extends MvpView {

    void showMainActivity();

    void onLoginFailed();

    void showProgress();

    void hideProgress();

    void onLoginSuccess();
}
