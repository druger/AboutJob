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

    public ReviewPresenter() {

    }

    @Inject
    public ReviewPresenter(RestApi restApi) {
        this.restApi = restApi;
    }

    private static final int NOT_SELECTED_STATUS = -1;
    private static final int WORKING_STATUS = 0;
    private static final int WORKED_STATUS = 1;
    private static final int INTERVIEW_STATUS = 2;

    protected int status = NOT_SELECTED_STATUS;

    protected Review review;
    protected MarkCompany mark;

    public Review getReview() {
        return review;
    }

    protected boolean isCorrectStatus() {
        return (status == WORKING_STATUS || status == WORKED_STATUS) && mark.getAverageMark() != 0
                || (status == INTERVIEW_STATUS && mark.getAverageMark() == 0);
    }

    protected boolean isCorrectReview(Review review) {
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

    public void setSalary(float rating) {
        mark.setSalary(rating);
    }

    public void setChief(float rating) {
        mark.setChief(rating);
    }

    public void setWorkplace(float rating) {
        mark.setWorkplace(rating);
    }

    public void setCareer(float rating) {
        mark.setCareer(rating);
    }

    public void setCollective(float rating) {
        mark.setCollective(rating);
    }

    public void setSocialPackage(float rating) {
        mark.setSocialPackage(rating);
    }

    //TODO сделать абстрактым(пока есть проблеммы с дагером)
    public void doneClick() {}
}
