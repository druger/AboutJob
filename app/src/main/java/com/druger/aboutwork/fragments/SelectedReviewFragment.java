package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.utils.Utils;

import static com.druger.aboutwork.Const.Bundles.FROM_ACCOUNT;
import static com.druger.aboutwork.Const.Bundles.NAME;
import static com.druger.aboutwork.Const.Bundles.REVIEW;
import static com.druger.aboutwork.Const.Colors.GRAY_500;

/**
 * A simple {@link Fragment} subclass.
 */
// TODO добавить MVP
public class SelectedReviewFragment extends BaseFragment implements View.OnClickListener {

    private TextView tvUserName;
    private TextView tvDate;
    private TextView tvCity;
    private TextView tvPosition;
    private TextView mPosition;
    private TextView tvEmploymentDate;
    private TextView mEmploymentDate;
    private TextView tvDismissalDate;
    private TextView mDismissalDate;
    private TextView tvInterviewDate;
    private TextView mInterviewDate;
    private TextView tvPluses;
    private TextView tvMinuses;
    private RatingBar salary;
    private RatingBar chief;
    private RatingBar workplace;
    private RatingBar career;
    private RatingBar collective;
    private RatingBar socialPackage;
    private ImageView ivLike;
    private ImageView ivDislike;
    private TextView tvLike;
    private TextView tvDislike;
    private ImageView ivComments;
    private Review review;
    private FloatingActionButton fabEdit;

    private Bundle bundle;
    private boolean fromAccount;

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
        bundle = getArguments();
        fromAccount = getArguments().getBoolean(FROM_ACCOUNT);

        if (!fromAccount) {
            rootView = inflater.inflate(R.layout.fragment_selected_review, container, false);
            setupToolbar();
        } else {
            rootView = inflater.inflate(R.layout.selected_review_no_actionbar, container, false);
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
        setUI();
        setUX();
        setReview();
        return rootView;
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
        tvUserName = bindView(R.id.tvUserName);
        tvDate =  bindView(R.id.tvDate);
        tvCity = bindView(R.id.tvCity);
        tvPosition = bindView(R.id.tvPosition);
        mPosition = bindView(R.id.tv_position);
        tvEmploymentDate = bindView(R.id.tvEmploymentDate);
        mEmploymentDate = bindView(R.id.tv_employment_date);
        tvDismissalDate = bindView(R.id.tvDismissalDate);
        mDismissalDate = bindView(R.id.tv_dismissal_date);
        tvInterviewDate = bindView(R.id.tvInterviewDate);
        mInterviewDate = bindView(R.id.tv_interview_date);
        tvPluses = bindView(R.id.tvPluses);
        tvMinuses = bindView(R.id.tvMinuses);

        salary = bindView(R.id.ratingbar_salary);
        chief = bindView(R.id.ratingbar_chief);
        workplace = bindView(R.id.ratingbar_workplace);
        career = bindView(R.id.ratingbar_career);
        collective = bindView(R.id.ratingbar_collective);
        socialPackage = bindView(R.id.ratingbar_social_package);

        ivLike = bindView(R.id.ivLike);
        ivDislike = bindView(R.id.ivDislike);
        ivComments = bindView(R.id.ivComments);
        tvLike = bindView(R.id.tvLike);
        tvDislike = bindView(R.id.tvDislike);

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
        review = bundle.getParcelable(REVIEW);

        if (review != null) {
            tvUserName.setText(review.getName());
            tvDate.setText(Utils.getDate(review.getDate()));
            tvCity.setText(review.getCity());
            if (!TextUtils.isEmpty(review.getPosition())) {
                tvPosition.setVisibility(View.VISIBLE);
                mPosition.setVisibility(View.VISIBLE);
                mPosition.setText(review.getPosition());
            }
            if (review.getEmploymentDate() != 0) {
                tvEmploymentDate.setVisibility(View.GONE);
                mEmploymentDate.setVisibility(View.GONE);
                mEmploymentDate.setText(String.valueOf(review.getEmploymentDate()));
            }
            if (review.getDismissalDate() != 0) {
                tvDismissalDate.setVisibility(View.GONE);
                mDismissalDate.setVisibility(View.GONE);
                mDismissalDate.setText(String.valueOf(review.getDismissalDate()));
            }
            if (review.getInterviewDate() != 0) {
                tvInterviewDate.setVisibility(View.GONE);
                mInterviewDate.setVisibility(View.GONE);
                mInterviewDate.setText(String.valueOf(review.getInterviewDate()));
            }
            tvPluses.setText(review.getPluses());
            tvMinuses.setText(review.getMinuses());

            salary.setRating(review.getMarkCompany().getSalary());
            chief.setRating(review.getMarkCompany().getChief());
            workplace.setRating(review.getMarkCompany().getWorkplace());
            career.setRating(review.getMarkCompany().getCareer());
            collective.setRating(review.getMarkCompany().getCollective());
            socialPackage.setRating(review.getMarkCompany().getSocialPackage());

            tvLike.setText(String.valueOf(review.getLike()));
            tvDislike.setText(String.valueOf(review.getDislike()));

            ivComments.setColorFilter(Color.parseColor(GRAY_500));

            boolean myLike = review.isMyLike();
            boolean myDislike = review.isMyDislike();
            if (!myLike) {
                ivLike.setTag(getActivity().getString(R.string.like_inactive));
            } else {
                ivLike.setTag(getActivity().getString(R.string.like_active));
                ivLike.setColorFilter(Color.parseColor("#8BC34A"));
            }
            if (!myDislike) {
                ivDislike.setTag(getActivity().getString(R.string.dislike_inactive));
            } else {
                ivDislike.setTag(getActivity().getString(R.string.dislike_active));
                ivDislike.setColorFilter(Color.parseColor("#F44336"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLike:
                clickLike();
                break;
            case R.id.tvDislike:
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
        int likeCount = review.getLike();
        int dislikeCount = review.getDislike();
        String tagLike = ivLike.getTag().toString();
        String tagDislike = ivDislike.getTag().toString();
        if (tagDislike.equalsIgnoreCase(getActivity().getString(R.string.dislike_inactive))) {
            ivDislike.setTag(getActivity().getString(R.string.dislike_active));
            ivDislike.setColorFilter(Color.parseColor("#F44336"));
            review.setDislike(++dislikeCount);
            review.setMyDislike(true);
            tvDislike.setText(String.valueOf(dislikeCount));
            FirebaseHelper.setDislike(review);

            if (tagLike.equalsIgnoreCase(getActivity().getString(R.string.like_active))) {
                ivLike.setTag(getActivity().getString(R.string.like_inactive));
                ivLike.setColorFilter(Color.parseColor(GRAY_500));
                review.setLike(--likeCount);
                review.setMyLike(false);
                tvLike.setText(String.valueOf(likeCount));
                FirebaseHelper.setLike(review);
            }
        } else {
            ivDislike.setTag(getActivity().getString(R.string.dislike_inactive));
            ivDislike.setColorFilter(Color.parseColor(GRAY_500));
            review.setDislike(--dislikeCount);
            review.setMyDislike(false);
            tvDislike.setText(String.valueOf(dislikeCount));
            FirebaseHelper.setDislike(review);
        }
    }

    private void clickLike() {
        int likeCount = review.getLike();
        int dislikeCount = review.getDislike();
        String tagLike = ivLike.getTag().toString();
        String tagDislike = ivDislike.getTag().toString();
        if (tagLike.equalsIgnoreCase(getActivity().getString(R.string.like_inactive))) {
            ivLike.setTag(getActivity().getString(R.string.like_active));
            ivLike.setColorFilter(Color.parseColor("#8BC34A"));
            review.setLike(++likeCount);
            review.setMyLike(true);
            tvLike.setText(String.valueOf(likeCount));
            FirebaseHelper.setLike(review);

            if (tagDislike.equalsIgnoreCase(getActivity().getString(R.string.dislike_active))) {
                ivDislike.setTag(getActivity().getString(R.string.dislike_inactive));
                ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                review.setDislike(--dislikeCount);
                review.setMyDislike(false);
                tvDislike.setText(String.valueOf(dislikeCount));
                FirebaseHelper.setDislike(review);
            }
        } else {
            ivLike.setTag(getActivity().getString(R.string.like_inactive));
            ivLike.setColorFilter(Color.parseColor(GRAY_500));
            review.setLike(--likeCount);
            review.setMyLike(false);
            tvLike.setText(String.valueOf(likeCount));
            FirebaseHelper.setLike(review);
        }

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
