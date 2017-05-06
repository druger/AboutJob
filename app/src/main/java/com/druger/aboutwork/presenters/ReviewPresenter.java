package com.druger.aboutwork.presenters;

import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.ReviewView;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

/**
 * Created by druger on 07.05.2017.
 */

@InjectViewState
public class ReviewPresenter extends MvpPresenter<ReviewView>
        implements RadioGroup.OnCheckedChangeListener{

    private int status = -1;

    private Review review;
    private MarkCompany mark;
    private String companyId;

    public ReviewPresenter() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        View radioButton;

        switch (checkedId) {
            case R.id.radio_working:
                getViewState().showWorkingDate();

                radioButton = group.findViewById(R.id.radio_working);
                status = group.indexOfChild(radioButton);
                break;
            case R.id.radio_worked:
                getViewState().showWorkedDate();

                radioButton = group.findViewById(R.id.radio_worked);
                status = group.indexOfChild(radioButton);
                break;
            case R.id.radio_interview:
                getViewState().showInterviewDate();

                radioButton = group.findViewById(R.id.radio_interview);
                status = group.indexOfChild(radioButton);
                break;
        }
    }

    public void setCompanyRating(RatingBar salary, RatingBar chief, RatingBar workplace, RatingBar career, RatingBar collective, RatingBar socialPackage) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            review = new Review(companyId, user.getUid(), Calendar.getInstance().getTimeInMillis());
            mark = new MarkCompany(user.getUid(), companyId);
        }

        salary.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setSalary(rating);
            }
        });

        chief.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setChief(rating);
            }
        });

        workplace.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setWorkplace(rating);
            }
        });

        career.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setCareer(rating);
            }
        });

        collective.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setCollective(rating);
            }
        });

        socialPackage.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mark.setSocialPackage(rating);
            }
        });

        review.setMarkCompany(mark);
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Review getReview() {
        return review;
    }

    public boolean checkReview(String pluses, String minuses, String position) {
        if (!TextUtils.isEmpty(pluses) && !TextUtils.isEmpty(minuses) && status > -1
                && mark.getAverageMark() != 0) {
            review.setPluses(pluses);
            review.setMinuses(minuses);
            review.setStatus(status);

            if (!TextUtils.isEmpty(position)) {
                review.setPosition(position);
            }
            return true;
        }
        return false;
    }
}
