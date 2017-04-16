package com.druger.aboutwork.ui.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
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

    private TextView userName;
    private TextView date;
    private TextView city;
    private TextView position;
    private TextView mPosition;
    private TextView employmentDate;
    private TextView mEmploymentDate;
    private TextView dismissalDate;
    private TextView mDismissalDate;
    private TextView interviewDate;
    private TextView mInterviewDate;
    private TextView pluses;
    private TextView minuses;
    private RatingBar salary;
    private RatingBar chief;
    private RatingBar workplace;
    private RatingBar career;
    private RatingBar collective;
    private RatingBar socialPackage;
    private ImageView like;
    private ImageView dislike;
    private TextView tvLike;
    private TextView tvDislike;
    private ImageView comments;

    private Review review;

    public SelectedReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_review, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String companyName = getActivity().getIntent().getStringExtra("name");
        if (companyName != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(companyName);
        }

        userName = (TextView) view.findViewById(R.id.user_name);
        date = (TextView) view.findViewById(R.id.date);
        city = (TextView) view.findViewById(R.id.city);
        position = (TextView) view.findViewById(R.id.position);
        mPosition = (TextView) view.findViewById(R.id.tv_position);
        employmentDate = (TextView) view.findViewById(R.id.employment_date);
        mEmploymentDate = (TextView) view.findViewById(R.id.tv_employment_date);
        dismissalDate = (TextView) view.findViewById(R.id.dismissal_date);
        mDismissalDate = (TextView) view.findViewById(R.id.tv_dismissal_date);
        interviewDate = (TextView) view.findViewById(R.id.interview_date);
        mInterviewDate = (TextView) view.findViewById(R.id.tv_interview_date);
        pluses = (TextView) view.findViewById(R.id.pluses);
        minuses = (TextView) view.findViewById(R.id.minuses);

        salary = (RatingBar) view.findViewById(R.id.ratingbar_salary);
        chief = (RatingBar) view.findViewById(R.id.ratingbar_chief);
        workplace = (RatingBar) view.findViewById(R.id.ratingbar_workplace);
        career = (RatingBar) view.findViewById(R.id.ratingbar_career);
        collective = (RatingBar) view.findViewById(R.id.ratingbar_collective);
        socialPackage = (RatingBar) view.findViewById(R.id.ratingbar_social_package);

        like = (ImageView) view.findViewById(R.id.like);
        dislike = (ImageView) view.findViewById(R.id.dislike);
        comments = (ImageView) view.findViewById(R.id.comments);
        tvLike = (TextView) view.findViewById(R.id.tvLike);
        tvDislike = (TextView) view.findViewById(R.id.tvDislike);

        setUI();
        return view;
    }

    private void setUI() {
        position.setVisibility(View.GONE);
        employmentDate.setVisibility(View.GONE);
        dismissalDate.setVisibility(View.GONE);
        interviewDate.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        review = bundle.getParcelable("review");

        if (review != null) {
            userName.setText(review.getName());
            date.setText(Utils.getDate(review.getDate()));
            city.setText(review.getCity());
            if (!TextUtils.isEmpty(review.getPosition())) {
                position.setVisibility(View.VISIBLE);
                mPosition.setVisibility(View.VISIBLE);
                mPosition.setText(review.getPosition());
            }
            if (review.getEmploymentDate() != 0) {
                employmentDate.setVisibility(View.GONE);
                mEmploymentDate.setVisibility(View.GONE);
                mEmploymentDate.setText(String.valueOf(review.getEmploymentDate()));
            }
            if (review.getDismissalDate() != 0) {
                dismissalDate.setVisibility(View.GONE);
                mDismissalDate.setVisibility(View.GONE);
                mDismissalDate.setText(String.valueOf(review.getDismissalDate()));
            }
            if (review.getInterviewDate() != 0) {
                interviewDate.setVisibility(View.GONE);
                mInterviewDate.setVisibility(View.GONE);
                mInterviewDate.setText(String.valueOf(review.getInterviewDate()));
            }
            pluses.setText(review.getPluses());
            minuses.setText(review.getMinuses());

            salary.setRating(review.getMarkCompany().getSalary());
            chief.setRating(review.getMarkCompany().getChief());
            workplace.setRating(review.getMarkCompany().getWorkplace());
            career.setRating(review.getMarkCompany().getCareer());
            collective.setRating(review.getMarkCompany().getCollective());
            socialPackage.setRating(review.getMarkCompany().getSocialPackage());

            tvLike.setText(String.valueOf(review.getLike()));
            tvDislike.setText(String.valueOf(review.getDislike()));

            like.setOnClickListener(this);
            dislike.setOnClickListener(this);
            comments.setOnClickListener(this);
            comments.setColorFilter(Color.parseColor("#9E9E9E"));

            boolean myLike = review.isMyLike();
            boolean myDislike = review.isMyDislike();
            if (!myLike) {
                like.setTag("likeInactive");
            } else {
                like.setTag("likeActive");
                like.setColorFilter(Color.parseColor("#8BC34A"));
            }
            if (!myDislike) {
                dislike.setTag("dislikeInactive");
            } else {
                dislike.setTag("dislikeActive");
                dislike.setColorFilter(Color.parseColor("#F44336"));
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
            case R.id.like:
                clickLike();
                break;
            case R.id.dislike:
                clickDislike();
                break;
            case R.id.comments:
                showComments();
                break;
        }
    }

    private void clickDislike() {
        int likeCount = review.getLike();
        int dislikeCount = review.getDislike();
        String tagLike = like.getTag().toString();
        String tagDislike = dislike.getTag().toString();
        if (tagDislike.equalsIgnoreCase("dislikeInactive")) {
            dislike.setTag("dislikeActive");
            dislike.setColorFilter(Color.parseColor("#F44336"));
            review.setDislike(++dislikeCount);
            review.setMyDislike(true);
            tvDislike.setText(String.valueOf(dislikeCount));
            FirebaseHelper.setDislike(review);

            if (tagLike.equalsIgnoreCase("likeActive")) {
                like.setTag("likeInactive");
                like.setColorFilter(Color.parseColor("#9E9E9E"));
                review.setLike(--likeCount);
                review.setMyLike(false);
                tvLike.setText(String.valueOf(likeCount));
                FirebaseHelper.setLike(review);
            }
        } else {
            dislike.setTag("dislikeInactive");
            dislike.setColorFilter(Color.parseColor("#9E9E9E"));
            review.setDislike(--dislikeCount);
            review.setMyDislike(false);
            tvDislike.setText(String.valueOf(dislikeCount));
            FirebaseHelper.setDislike(review);
        }
    }

    private void clickLike() {
        int likeCount = review.getLike();
        int dislikeCount = review.getDislike();
        String tagLike = like.getTag().toString();
        String tagDislike = dislike.getTag().toString();
        if (tagLike.equalsIgnoreCase("likeInactive")) {
            like.setTag("likeActive");
            like.setColorFilter(Color.parseColor("#8BC34A"));
            review.setLike(++likeCount);
            review.setMyLike(true);
            tvLike.setText(String.valueOf(likeCount));
            FirebaseHelper.setLike(review);

            if (tagDislike.equalsIgnoreCase("dislikeActive")) {
                dislike.setTag("dislikeInactive");
                dislike.setColorFilter(Color.parseColor("#9E9E9E"));
                review.setDislike(--dislikeCount);
                review.setMyDislike(false);
                tvDislike.setText(String.valueOf(dislikeCount));
                FirebaseHelper.setDislike(review);
            }
        } else {
            like.setTag("likeInactive");
            like.setColorFilter(Color.parseColor("#9E9E9E"));
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
}
