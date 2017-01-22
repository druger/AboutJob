package com.druger.aboutwork.ui.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.druger.aboutwork.AboutWorkApp;
import com.druger.aboutwork.R;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.utils.Utils;
import com.squareup.leakcanary.RefWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedReviewFragment extends Fragment {


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

        TextView userName = (TextView) view.findViewById(R.id.user_name);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView city = (TextView) view.findViewById(R.id.city);
        TextView position = (TextView) view.findViewById(R.id.position);
        TextView mPosition = (TextView) view.findViewById(R.id.tv_position);
        TextView employmentDate = (TextView) view.findViewById(R.id.employment_date);
        TextView mEmploymentDate = (TextView) view.findViewById(R.id.tv_employment_date);
        TextView dismissalDate = (TextView) view.findViewById(R.id.dismissal_date);
        TextView mDismissalDate = (TextView) view.findViewById(R.id.tv_dismissal_date);
        TextView interviewDate = (TextView) view.findViewById(R.id.interview_date);
        TextView mInterviewDate = (TextView) view.findViewById(R.id.tv_interview_date);
        TextView pluses = (TextView) view.findViewById(R.id.pluses);
        TextView minuses = (TextView) view.findViewById(R.id.minuses);

        RatingBar salary = (RatingBar) view.findViewById(R.id.ratingbar_salary);
        RatingBar chief = (RatingBar) view.findViewById(R.id.ratingbar_chief);
        RatingBar workplace = (RatingBar) view.findViewById(R.id.ratingbar_workplace);
        RatingBar career = (RatingBar) view.findViewById(R.id.ratingbar_career);
        RatingBar collective = (RatingBar) view.findViewById(R.id.ratingbar_collective);
        RatingBar socialPackage = (RatingBar) view.findViewById(R.id.ratingbar_social_package);

        position.setVisibility(View.GONE);
        employmentDate.setVisibility(View.GONE);
        dismissalDate.setVisibility(View.GONE);
        interviewDate.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        Review review = bundle.getParcelable("review");

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
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AboutWorkApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
