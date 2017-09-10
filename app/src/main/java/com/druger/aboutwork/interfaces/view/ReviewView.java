package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;

/**
 * Created by druger on 07.05.2017.
 */

public interface ReviewView extends MvpView {

    void showWorkingDate();

    void showWorkedDate();

    void showInterviewDate();

    void successfulAddition();

    void showErrorAdding();

    void setIsIndicatorRatingBar(boolean indicator);

    void clearRatingBar();
}
