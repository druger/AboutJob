package com.druger.aboutwork.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.presenters.UserReviewsPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserReviewsFragment extends BaseFragment {

    @InjectPresenter
    UserReviewsPresenter userReviewsPresenter;


    public UserReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_reviews, container, false);
    }

}
