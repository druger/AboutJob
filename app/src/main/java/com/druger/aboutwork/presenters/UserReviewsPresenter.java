package com.druger.aboutwork.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.UserReviews;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by druger on 31.01.2018.
 */

@InjectViewState
public class UserReviewsPresenter extends BasePresenter<UserReviews> {

    private FirebaseStorage storage;
    private StorageReference storageRef;

    public UserReviewsPresenter() {
        storage = FirebaseStorage.getInstance();
    }

    public void downloadPhoto(String userId) {
        storageRef = FirebaseHelper.downloadPhoto(storage, userId);
        getViewState().showPhoto(storageRef);
    }
}
