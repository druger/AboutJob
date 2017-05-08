package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

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
}
