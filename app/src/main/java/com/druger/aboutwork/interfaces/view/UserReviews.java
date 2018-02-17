package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.model.Review;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by druger on 31.01.2018.
 */

public interface UserReviews extends MvpView {

    void showPhoto(StorageReference storageRef);

    void notifyDataSetChanged();

    void showReviews(List<Review> reviews);

    void showName(String name);
}
