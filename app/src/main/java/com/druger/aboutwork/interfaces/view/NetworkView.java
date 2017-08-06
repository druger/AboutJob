package com.druger.aboutwork.interfaces.view;

import com.druger.aboutwork.enums.TypeMessage;

/**
 * Created by druger on 06.08.2017.
 */

public interface NetworkView {

    void showProgress(boolean show);

    void showMessage(String message, TypeMessage typeMessage);
}
