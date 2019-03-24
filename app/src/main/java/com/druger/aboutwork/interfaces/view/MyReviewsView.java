package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.model.Review;

import java.util.List;

/**
 * Created by druger on 09.05.2017.
 */

public interface MyReviewsView extends MvpView, NetworkView {

    void showReviews(List<Review> reviews);

    void notifyItemInserted(int position);
}
