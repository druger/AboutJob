package com.druger.aboutwork.presenters;

import android.text.TextUtils;
import android.widget.RatingBar;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.ReviewView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.rest.RestApi;
import com.druger.aboutwork.rest.models.CityResponse;
import com.druger.aboutwork.rest.models.VacancyResponse;
import com.druger.aboutwork.utils.rx.RxUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by druger on 07.05.2017.
 */

@InjectViewState
public class ReviewPresenter extends BasePresenter<ReviewView> {

    @Inject
    public ReviewPresenter(RestApi restApi) {
        this.restApi = restApi;
    }

    private static final int NOT_SELECTED_STATUS = -1;
    private static final int WORKING_STATUS = 0;
    private static final int WORKED_STATUS = 1;
    private static final int INTERVIEW_STATUS = 2;

    private int status = NOT_SELECTED_STATUS;

    private Review review;
    private MarkCompany mark;
    private String companyId;

    // TODO сократить кол-во параметров
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

        if (this.review != null) {
            this.review.setMarkCompany(mark);
        }
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Review getReview() {
        return review;
    }

    // TODO: отрефакторить метод
    public void checkReview(Review review, Company company, boolean fromAccount) {
        if (isCorrectStatus() && isCorrectReview(review)) {

            review.setStatus(status);

            if (fromAccount) {
                FirebaseHelper.updateReview(review);
            } else {
                FirebaseHelper.addReview(review);
                FirebaseHelper.addCompany(company);
                getViewState().successfulAddition();
            }

        } else {
            getViewState().showErrorAdding();
        }
    }

    private boolean isCorrectStatus() {
        return (status == WORKING_STATUS || status == WORKED_STATUS) && mark.getAverageMark() != 0
                || (status == INTERVIEW_STATUS && mark.getAverageMark() == 0);
    }

    private boolean isCorrectReview(Review review) {
        return !TextUtils.isEmpty(review.getPluses()) && !TextUtils.isEmpty(review.getMinuses())
                && !TextUtils.isEmpty(review.getPosition()) && !TextUtils.isEmpty(review.getCity());
    }

    public void getCities(String city) {
        queryGetCities(city);
    }

    private void queryGetCities(String city) {
        Disposable request = restApi.cities.getCities(city)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCities, this::handleError);

        unSubscribeOnDestroy(request);
    }

    private void successGetCities(CityResponse cityResponse) {
        getViewState().showCities(cityResponse.getItems());
    }

    public void getVacancies(String vacancy) {
        Disposable request = restApi.vacancies.getVacancies(vacancy)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetVacancies, this::handleError);

        unSubscribeOnDestroy(request);
    }

    private void successGetVacancies(VacancyResponse vacancyResponse) {
        getViewState().showVacancies(vacancyResponse.getItems());
    }

    public void onSelectedWorkingStatus(int position) {
        getViewState().showWorkingDate();

        status = position;
        getViewState().setIsIndicatorRatingBar(false);
    }

    public void onSelectedWorkedStatus(int position) {
        getViewState().showWorkedDate();

        status = position;
        getViewState().setIsIndicatorRatingBar(false);
    }

    public void onSelectedInterviewStatus(int position) {
        getViewState().showInterviewDate();

        status = position;
        getViewState().setIsIndicatorRatingBar(true);
        getViewState().clearRatingBar();
    }
}
