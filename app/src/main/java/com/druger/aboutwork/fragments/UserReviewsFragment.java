package com.druger.aboutwork.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.UserReviews;
import com.druger.aboutwork.presenters.UserReviewsPresenter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.druger.aboutwork.Const.Bundles.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserReviewsFragment extends BaseFragment implements UserReviews {

    @InjectPresenter
    UserReviewsPresenter reviewsPresenter;

    private CircleImageView civAvatar;
    private TextureView tvName;
    private TextureView tvCountReviews;
    private RecyclerView rvReviews;

    public UserReviewsFragment() {
        // Required empty public constructor
    }

    public static UserReviewsFragment newInstance(String userId) {

        Bundle args = new Bundle();

        UserReviewsFragment fragment = new UserReviewsFragment();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_reviews, container, false);
        setupUI();
        setupRecycler();
        reviewsPresenter.downloadPhoto(getArguments().getString(USER_ID));
        return rootView;
    }

    private void setupRecycler() {

    }

    private void setupUI() {
        civAvatar = bindView(R.id.civAvatar);
        tvName = bindView(R.id.tvName);
        tvCountReviews = bindView(R.id.tvCountReviews);
        rvReviews = bindView(R.id.rvReviews);
    }

    @Override
    public void showPhoto(StorageReference storageRef) {
        Glide.with(getActivity())
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .crossFade()
                .error(R.drawable.ic_account_circle_black)
                .into(civAvatar);
    }
}
