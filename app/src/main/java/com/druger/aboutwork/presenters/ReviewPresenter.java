package com.druger.aboutwork.presenters;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
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

    private static final int NOT_SELECTED_STATUS = -1;
    private static final int WORKING_STATUS = 0;
    private static final int WORKED_STATUS = 1;
    private static final int INTERVIEW_STATUS = 2;

    private int status = NOT_SELECTED_STATUS;

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
                getViewState().setIsIndicatorRatingBar(false);
                break;
            case R.id.radio_worked:
                getViewState().showWorkedDate();

                radioButton = group.findViewById(R.id.radio_worked);
                status = group.indexOfChild(radioButton);
                getViewState().setIsIndicatorRatingBar(false);
                break;
            case R.id.radio_interview:
                getViewState().showInterviewDate();

                radioButton = group.findViewById(R.id.radio_interview);
                status = group.indexOfChild(radioButton);
                getViewState().setIsIndicatorRatingBar(true);
                getViewState().clearRatingBar();
                break;
        }
    }

    public void setCompanyRating(RatingBar salary, RatingBar chief, RatingBar workplace,
                                 RatingBar career, RatingBar collective, RatingBar socialPackage,
                                 Review review, boolean fromAccount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (fromAccount) {
                this.review = review;
                mark = review.getMarkCompany();
            } else {
                this.review = new Review(companyId, user.getUid(), Calendar.getInstance().getTimeInMillis());
                mark = new MarkCompany(user.getUid(), companyId);
            }
        }

        salary.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> mark.setSalary(rating));
        chief.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> mark.setChief(rating));
        workplace.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> mark.setWorkplace(rating));
        career.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> mark.setCareer(rating));
        collective.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> mark.setCollective(rating));
        socialPackage.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> mark.setSocialPackage(rating));

        this.review.setMarkCompany(mark);
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Review getReview() {
        return review;
    }

    public void checkReview(String pluses, String minuses, String position,
                            @Nullable String companyId, @Nullable String companyName,
                            boolean fromAccount) {
        if (((status == WORKING_STATUS || status == WORKED_STATUS) && mark.getAverageMark() != 0)
                || (status == INTERVIEW_STATUS  && mark.getAverageMark() == 0)) {
            if (!TextUtils.isEmpty(pluses) && !TextUtils.isEmpty(minuses)) {
                review.setPluses(pluses);
                review.setMinuses(minuses);
                review.setStatus(status);

                if (!TextUtils.isEmpty(position)) {
                    review.setPosition(position);
                }
                if (fromAccount) {
                    FirebaseHelper.updateReview(review);
                } else {
                    FirebaseHelper.addReview(review);
                    FirebaseHelper.addCompany(companyId, companyName);
                    getViewState().successfulAddition();
                }
            }
        } else {
            getViewState().showErrorAdding();
        }
    }
}
