package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.google.firebase.storage.StorageReference;

/**
 * Created by druger on 31.01.2018.
 */

public interface UserReviews extends MvpView {

    void showPhoto(StorageReference storageRef);
}
