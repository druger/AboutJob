package com.druger.aboutwork.fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.druger.aboutwork.R;
import com.druger.aboutwork.adapters.MyReviewAdapter;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.interfaces.view.UserReviews;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.UserReviewsPresenter;
import com.druger.aboutwork.utils.recycler.EndlessRecyclerViewScrollListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.druger.aboutwork.Const.Bundles.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserReviewsFragment extends BaseFragment implements UserReviews {

    @InjectPresenter
    UserReviewsPresenter reviewsPresenter;

    private CircleImageView civAvatar;
    private TextView tvName;
    private TextView tvCountReviews;

    private RecyclerView rvReviews;
    private MyReviewAdapter reviewAdapter;
    private List<Review> reviews = new ArrayList<>();

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
        setupRecycler(reviews);
        reviewsPresenter.downloadPhoto(getArguments().getString(USER_ID));
        reviewsPresenter.fetchReviews(getArguments().getString(USER_ID), 1);
        reviewsPresenter.getUserName(getArguments().getString(USER_ID));
        setupToolbar();
        return rootView;
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecycler(final List<Review> reviews) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        reviewAdapter = new MyReviewAdapter(reviews);
        rvReviews.setLayoutManager(layoutManager);
        rvReviews.setItemAnimator(new DefaultItemAnimator());
        rvReviews.setAdapter(reviewAdapter);

        reviewAdapter.setOnClickListener(new OnItemClickListener<Review>() {
            @Override
            public void onClick(Review review, int position) {
                SelectedReviewFragment reviewFragment = SelectedReviewFragment.newInstance(review, false);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.company_container, reviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });


        rvReviews.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                reviewsPresenter.fetchReviews(getArguments().getString(USER_ID), ++page);
            }
        });
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

    @Override
    public void notifyDataSetChanged() {
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showReviews(List<Review> reviews) {
        tvCountReviews.setText(String.valueOf(reviews.size()));
        this.reviews.clear();
        this.reviews.addAll(reviews);
        reviewAdapter.notifyDataSetChanged();

    }

    @Override
    public void showName(String name) {
        tvName.setText(name);
        getActionBar().setTitle(name);
    }

    @Override
    public void onStop() {
        super.onStop();
        reviewsPresenter.removeListeners();
    }
}
