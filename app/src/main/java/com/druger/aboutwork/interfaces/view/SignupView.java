package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;

/**
 * Created by druger on 13.05.2017.
 */

public interface SignupView extends MvpView {

    void onSignupFailed();

    void showProgress();

    void hideProgress();

    void onSignupSuccess();
}
