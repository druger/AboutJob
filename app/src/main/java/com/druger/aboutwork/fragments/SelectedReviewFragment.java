package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.databinding.FragmentSelectedReviewBinding;
import com.druger.aboutwork.databinding.SelectedReviewNoActionbarBinding;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Review;

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

    private TextView tvPosition;
    private TextView mPosition;
    private TextView tvEmploymentDate;
    private TextView mEmploymentDate;
    private TextView tvDismissalDate;
    private TextView mDismissalDate;
    private TextView tvInterviewDate;
    private TextView mInterviewDate;
    private ImageView ivLike;
    private ImageView ivDislike;
    private ImageView ivComments;
    private Review review;
    private FloatingActionButton fabEdit;

    private Bundle bundle;
    private boolean fromAccount;
    private FragmentSelectedReviewBinding binding;
    private SelectedReviewNoActionbarBinding bindingNoBar;

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
        return rootView;
    }

    private void getBundles() {
        bundle = getArguments();
        review = bundle.getParcelable(REVIEW);
        fromAccount = getArguments().getBoolean(FROM_ACCOUNT);
    }

    private void setView(LayoutInflater inflater, ViewGroup container) {
        if (!fromAccount) {
            binding = DataBindingUtil
                    .inflate(inflater, R.layout.fragment_selected_review, container, false);
            binding.setReview(review);
            binding.setMarkCompany(review.getMarkCompany());
            rootView = binding.getRoot();
            setupToolbar();
        } else {
            bindingNoBar = DataBindingUtil
                    .inflate(inflater, R.layout.selected_review_no_actionbar, container, false);
            bindingNoBar.setReview(review);
            bindingNoBar.setMarkCompany(review.getMarkCompany());
            rootView = bindingNoBar.getRoot();
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
    }

    private void setUX() {
        ivLike.setOnClickListener(this);
        ivDislike.setOnClickListener(this);
        ivComments.setOnClickListener(this);
        if (fromAccount) {
            fabEdit.setOnClickListener(this);
        }
    }

    private void setUI() {
        tvPosition = bindView(R.id.tvPosition);
        mPosition = bindView(R.id.tv_position);
        tvEmploymentDate = bindView(R.id.tvEmploymentDate);
        mEmploymentDate = bindView(R.id.tv_employment_date);
        tvDismissalDate = bindView(R.id.tvDismissalDate);
        mDismissalDate = bindView(R.id.tv_dismissal_date);
        tvInterviewDate = bindView(R.id.tvInterviewDate);
        mInterviewDate = bindView(R.id.tv_interview_date);

        ivLike = bindView(R.id.ivLike);
        ivDislike = bindView(R.id.ivDislike);
        ivComments = bindView(R.id.ivComments);

        fabEdit = bindView(R.id.fabEdit);

        tvPosition.setVisibility(View.GONE);
        tvEmploymentDate.setVisibility(View.GONE);
        tvDismissalDate.setVisibility(View.GONE);
        tvInterviewDate.setVisibility(View.GONE);
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
            if (!TextUtils.isEmpty(review.getPosition())) {
                tvPosition.setVisibility(View.VISIBLE);
                mPosition.setVisibility(View.VISIBLE);
            }
            if (review.getEmploymentDate() != 0) {
                tvEmploymentDate.setVisibility(View.GONE);
                mEmploymentDate.setVisibility(View.GONE);
            }
            if (review.getDismissalDate() != 0) {
                tvDismissalDate.setVisibility(View.GONE);
                mDismissalDate.setVisibility(View.GONE);
            }
            if (review.getInterviewDate() != 0) {
                tvInterviewDate.setVisibility(View.GONE);
                mInterviewDate.setVisibility(View.GONE);
            }
            ivComments.setColorFilter(Color.parseColor(GRAY_500));

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
            case R.id.ivComments:
                showComments();
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
        CommentsFragment comments;

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (getArguments().getBoolean(FROM_ACCOUNT)) {
            comments = CommentsFragment.newInstance(review.getFirebaseKey(), true);
            transaction.replace(R.id.main_container, comments);
        } else {
            comments = CommentsFragment.newInstance(review.getFirebaseKey(), false);
            transaction.replace(R.id.company_container, comments);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fromAccount) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }
}
