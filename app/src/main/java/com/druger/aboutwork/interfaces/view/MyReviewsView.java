package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.druger.aboutwork.model.Review;

import java.util.List;

/**
 * Created by druger on 09.05.2017.
 */
@StateStrategyType(OneExecutionStateStrategy.class)
public interface MyReviewsView extends MvpView, NetworkView {

    void showReviews(List<Review> reviews);
}
