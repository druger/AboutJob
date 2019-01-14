package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.adapters.CommentAdapter;
import com.druger.aboutwork.databinding.FragmentSelectedReviewBinding;
import com.druger.aboutwork.databinding.SelectedReviewNoActionbarBinding;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.presenters.SelectedReviewPresenter;

import static com.druger.aboutwork.Const.Bundles.FROM_ACCOUNT;
import static com.druger.aboutwork.Const.Bundles.NAME;
import static com.druger.aboutwork.Const.Bundles.REVIEW;
import static com.druger.aboutwork.Const.Colors.GRAY_500;
import static com.druger.aboutwork.Const.Colors.GREEN_500;
import static com.druger.aboutwork.Const.Colors.RED_500;

/**
 * A simple {@link Fragment} subclass.
 */
// TODO добавить MVP, ValueEventListener from Firebase
public class SelectedReviewFragment extends BaseFragment implements View.OnClickListener {

    @InjectPresenter
    SelectedReviewPresenter presenter;

    private TextView tvDescriptionStatus;
    private ImageView ivLike;
    private ImageView ivDislike;
    private Review review;
    private FloatingActionButton fabEdit;
    private EditText etMessage;
    private ImageView ivSend;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;

    private Bundle bundle;
    private boolean editMode;

    public SelectedReviewFragment() {
        // Required empty public constructor
    }

    public static SelectedReviewFragment newInstance(Review review, boolean fromAccount) {

        Bundle args = new Bundle();
        args.putParcelable(REVIEW, review);
        args.putBoolean(FROM_ACCOUNT, fromAccount);

        SelectedReviewFragment fragment = new SelectedReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getBundles();
        setView(inflater, container);
        setUI();
        setUX();
        setReview();
        showComments();
        return rootView;
    }

    private void getBundles() {
        bundle = getArguments();
        review = bundle.getParcelable(REVIEW);
        editMode = getArguments().getBoolean(FROM_ACCOUNT);
    }

    private void setView(LayoutInflater inflater, ViewGroup container) {
        if (!editMode) {
            FragmentSelectedReviewBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.fragment_selected_review, container, false);
            binding.setReview(review);
            rootView = binding.getRoot();
            setupToolbar();
        } else {
            SelectedReviewNoActionbarBinding bindingNoBar = DataBindingUtil
                    .inflate(inflater, R.layout.selected_review_no_actionbar, container, false);
            bindingNoBar.setReview(review);
            rootView = bindingNoBar.getRoot();
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
    }

    private void setUX() {
        ivLike.setOnClickListener(this);
        ivDislike.setOnClickListener(this);
        if (editMode) {
            fabEdit.setOnClickListener(this);
        }
    }

    private void setUI() {
        tvDescriptionStatus = bindView(R.id.tvDescriptionStatus);
        ivLike = bindView(R.id.ivLike);
        ivDislike = bindView(R.id.ivDislike);
        fabEdit = bindView(R.id.fabEdit);
        etMessage = bindView(R.id.etMessage);
        ivSend = bindView(R.id.ivSend);
        rvComments = bindView(R.id.rvComments);
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String companyName = getActivity().getIntent().getStringExtra(NAME);
        if (companyName != null) {
            getActionBar().setTitle(companyName);
        }
    }

    private void setReview() {
        if (review != null) {
            boolean myLike = review.isMyLike();
            boolean myDislike = review.isMyDislike();
            if (!myLike) {
                ivLike.setTag(getActivity().getString(R.string.like_inactive));
            } else {
                ivLike.setTag(getActivity().getString(R.string.like_active));
                ivLike.setColorFilter(Color.parseColor(GREEN_500));
            }
            if (!myDislike) {
                ivDislike.setTag(getActivity().getString(R.string.dislike_inactive));
            } else {
                ivDislike.setTag(getActivity().getString(R.string.dislike_active));
                ivDislike.setColorFilter(Color.parseColor(RED_500));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLike:
                clickLike();
                break;
            case R.id.ivDislike:
                clickDislike();
                break;
            case R.id.fabEdit:
                showEditReview();
                break;
            default:
                break;
        }
    }

    private void showEditReview() {
        ReviewFragment reviewFragment = ReviewFragment.newInstance(review, true);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, reviewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void clickDislike() {
        int like = review.getLike();
        int dislike = review.getDislike();
        if (!review.isMyDislike()) {
            ivDislike.setColorFilter(Color.parseColor(RED_500));
            review.setDislike(++dislike);
            review.setMyDislike(true);

            if (review.isMyLike()) {
                ivLike.setColorFilter(Color.parseColor(GRAY_500));
                review.setLike(--like);
                review.setMyLike(false);
                FirebaseHelper.likeReview(review);
            }
        } else {
            ivDislike.setColorFilter(Color.parseColor(GRAY_500));
            review.setDislike(--dislike);
            review.setMyDislike(false);
        }
        FirebaseHelper.dislikeReview(review);
    }

    private void clickLike() {
        int like = review.getLike();
        int dislike = review.getDislike();
        if (!review.isMyLike()) {
            ivLike.setColorFilter(Color.parseColor(GREEN_500));
            review.setLike(++like);
            review.setMyLike(true);

            if (review.isMyDislike()) {
                ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                review.setDislike(--dislike);
                review.setMyDislike(false);
                FirebaseHelper.dislikeReview(review);
            }
        } else {
            ivLike.setColorFilter(Color.parseColor(GRAY_500));
            review.setLike(--like);
            review.setMyLike(false);
        }
        FirebaseHelper.likeReview(review);
    }

    private void showComments() {
        commentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvComments.setItemAnimator(new DefaultItemAnimator());
        rvComments.setAdapter(commentAdapter);
        commentAdapter.setOnNameClickListener(comment -> showReviews(comment.getUserId()));
    }

    private void showReviews(String userId) {
        UserReviewsFragment reviews = UserReviewsFragment.newInstance(userId);

        FragmentTransaction transaction =getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.company_container, reviews);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (editMode) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }
}
