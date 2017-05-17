package com.druger.aboutwork.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.utils.Utils;
import com.squareup.leakcanary.RefWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedReviewFragment extends Fragment implements View.OnClickListener {

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
    private BottomNavigationView bottomNavigationView;
    private Review review;

    public SelectedReviewFragment() {
        // Required empty public constructor
    }

    public static SelectedReviewFragment newInstance(Review review, boolean fromAccount) {

        Bundle args = new Bundle();
        args.putParcelable("review", review);
        args.putBoolean("fromAccount", fromAccount);

        SelectedReviewFragment fragment = new SelectedReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (!getArguments().getBoolean("fromAccount")) {
            view = inflater.inflate(R.layout.fragment_selected_review, container, false);
            setToolbar(view);
        } else {
            view = inflater.inflate(R.layout.selected_review_no_actionbar, container, false);
        }
        setUI(view);
        setUX();
        setReview();
        hideViews();
        return view;
    }

    private void hideViews() {
        if (getArguments().getBoolean("fromAccount")) {
            bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.INVISIBLE);
        }
    }

    private void setUX() {
        ivLike.setOnClickListener(this);
        ivDislike.setOnClickListener(this);
        ivComments.setOnClickListener(this);
    }

    private void setUI(View view) {
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvCity = (TextView) view.findViewById(R.id.tvCity);
        tvPosition = (TextView) view.findViewById(R.id.tvPosition);
        mPosition = (TextView) view.findViewById(R.id.tv_position);
        tvEmploymentDate = (TextView) view.findViewById(R.id.tvEmploymentDate);
        mEmploymentDate = (TextView) view.findViewById(R.id.tv_employment_date);
        tvDismissalDate = (TextView) view.findViewById(R.id.tvDismissalDate);
        mDismissalDate = (TextView) view.findViewById(R.id.tv_dismissal_date);
        tvInterviewDate = (TextView) view.findViewById(R.id.tvInterviewDate);
        mInterviewDate = (TextView) view.findViewById(R.id.tv_interview_date);
        tvPluses = (TextView) view.findViewById(R.id.tvPluses);
        tvMinuses = (TextView) view.findViewById(R.id.tvMinuses);

        salary = (RatingBar) view.findViewById(R.id.ratingbar_salary);
        chief = (RatingBar) view.findViewById(R.id.ratingbar_chief);
        workplace = (RatingBar) view.findViewById(R.id.ratingbar_workplace);
        career = (RatingBar) view.findViewById(R.id.ratingbar_career);
        collective = (RatingBar) view.findViewById(R.id.ratingbar_collective);
        socialPackage = (RatingBar) view.findViewById(R.id.ratingbar_social_package);

        ivLike = (ImageView) view.findViewById(R.id.ivLike);
        ivDislike = (ImageView) view.findViewById(R.id.ivDislike);
        ivComments = (ImageView) view.findViewById(R.id.ivComments);
        tvLike = (TextView) view.findViewById(R.id.tvLike);
        tvDislike = (TextView) view.findViewById(R.id.tvDislike);

        tvPosition.setVisibility(View.GONE);
        tvEmploymentDate.setVisibility(View.GONE);
        tvDismissalDate.setVisibility(View.GONE);
        tvInterviewDate.setVisibility(View.GONE);
    }

    private void setToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String companyName = getActivity().getIntent().getStringExtra("name");
        if (companyName != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(companyName);
        }
    }

    private void setReview() {
        Bundle bundle = getArguments();
        review = bundle.getParcelable("review");

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

            ivComments.setColorFilter(Color.parseColor("#9E9E9E"));

            boolean myLike = review.isMyLike();
            boolean myDislike = review.isMyDislike();
            if (!myLike) {
                ivLike.setTag("likeInactive");
            } else {
                ivLike.setTag("likeActive");
                ivLike.setColorFilter(Color.parseColor("#8BC34A"));
            }
            if (!myDislike) {
                ivDislike.setTag("dislikeInactive");
            } else {
                ivDislike.setTag("dislikeActive");
                ivDislike.setColorFilter(Color.parseColor("#F44336"));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
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
        }
    }

    private void clickDislike() {
        int likeCount = review.getLike();
        int dislikeCount = review.getDislike();
        String tagLike = ivLike.getTag().toString();
        String tagDislike = ivDislike.getTag().toString();
        if (tagDislike.equalsIgnoreCase("dislikeInactive")) {
            ivDislike.setTag("dislikeActive");
            ivDislike.setColorFilter(Color.parseColor("#F44336"));
            review.setDislike(++dislikeCount);
            review.setMyDislike(true);
            tvDislike.setText(String.valueOf(dislikeCount));
            FirebaseHelper.setDislike(review);

            if (tagLike.equalsIgnoreCase("likeActive")) {
                ivLike.setTag("likeInactive");
                ivLike.setColorFilter(Color.parseColor("#9E9E9E"));
                review.setLike(--likeCount);
                review.setMyLike(false);
                tvLike.setText(String.valueOf(likeCount));
                FirebaseHelper.setLike(review);
            }
        } else {
            ivDislike.setTag("dislikeInactive");
            ivDislike.setColorFilter(Color.parseColor("#9E9E9E"));
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
        if (tagLike.equalsIgnoreCase("likeInactive")) {
            ivLike.setTag("likeActive");
            ivLike.setColorFilter(Color.parseColor("#8BC34A"));
            review.setLike(++likeCount);
            review.setMyLike(true);
            tvLike.setText(String.valueOf(likeCount));
            FirebaseHelper.setLike(review);

            if (tagDislike.equalsIgnoreCase("dislikeActive")) {
                ivDislike.setTag("dislikeInactive");
                ivDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                review.setDislike(--dislikeCount);
                review.setMyDislike(false);
                tvDislike.setText(String.valueOf(dislikeCount));
                FirebaseHelper.setDislike(review);
            }
        } else {
            ivLike.setTag("likeInactive");
            ivLike.setColorFilter(Color.parseColor("#9E9E9E"));
            review.setLike(--likeCount);
            review.setMyLike(false);
            tvLike.setText(String.valueOf(likeCount));
            FirebaseHelper.setLike(review);
        }

    }

    private void showComments() {
        CommentsFragment comments = CommentsFragment.newInstance(review.getFirebaseKey());

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.company_container, comments);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}
