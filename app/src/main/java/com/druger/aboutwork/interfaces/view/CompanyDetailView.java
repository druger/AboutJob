package com.druger.aboutwork.interfaces.view;

import com.arellomobile.mvp.MvpView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.Review;

import java.util.List;

/**
 * Created by druger on 01.05.2017.
 */

public interface CompanyDetailView extends MvpView, NetworkView {

    void updateAdapter();

    void showReviews(List<Review> reviews);

    void showCompanyDetail(CompanyDetail company);

    void showProgressReview();

    void hideProgressReview();
}
