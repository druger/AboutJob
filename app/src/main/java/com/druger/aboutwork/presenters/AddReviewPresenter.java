package com.druger.aboutwork.presenters;

import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.AddReviewView;
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

import static com.druger.aboutwork.Const.ReviewStatus.INTERVIEW_STATUS;
import static com.druger.aboutwork.Const.ReviewStatus.NOT_SELECTED_STATUS;
import static com.druger.aboutwork.Const.ReviewStatus.WORKED_STATUS;
import static com.druger.aboutwork.Const.ReviewStatus.WORKING_STATUS;

@InjectViewState
public class AddReviewPresenter extends BasePresenter<AddReviewView> {

    @Inject
    public AddReviewPresenter(RestApi restApi) {
        this.restApi = restApi;
    }

    private int status = NOT_SELECTED_STATUS;

    public String companyId;
    public String companyName;

    public Review review;
    private MarkCompany mark;

    public void setupReview() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        review = new Review(companyId, user.getUid(), Calendar.getInstance().getTimeInMillis());
        mark = new MarkCompany(user.getUid(), companyId);
        review.setMarkCompany(mark);
    }

    public void doneClick() {
        Company company = new Company(companyId, companyName);
        addReview(company);
    }

    private void addReview(Company company) {
        if (isCorrectStatus() && isCorrectReview(review)) {
            review.setStatus(status);
            FirebaseHelper.INSTANCE.addReview(review);
            FirebaseHelper.INSTANCE.addCompany(company);
            getViewState().successfulAddition();
        } else {
            getViewState().showErrorAdding();
        }
    }

    private Boolean isCorrectReview(Review review) {
        return (!TextUtils.isEmpty(review.getPluses()) && !TextUtils.isEmpty(review.getMinuses())
                && !TextUtils.isEmpty(review.getPosition()) && !TextUtils.isEmpty(review.getCity()));
    }

    private Boolean isCorrectStatus() {
        return (status == WORKING_STATUS || status == WORKED_STATUS) && mark.getAverageMark() != 0F
                || status == INTERVIEW_STATUS && mark.getAverageMark() == 0F;
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

    public void getVacancies(String vacancy) {
        Disposable request = restApi.getVacancies().getVacancies(vacancy)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this:: successGetVacancies, this::handleError);
        unSubscribeOnDestroy(request);
    }

    private void successGetVacancies(VacancyResponse vacancyResponse) {
        getViewState().showVacancies(vacancyResponse.getItems());
    }

    public void getCities(String city) {
        Disposable request = restApi.getCities().getCities(city)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCities, this::handleError);
        unSubscribeOnDestroy(request);
    }

    private void successGetCities(CityResponse cityResponse) {
        getViewState().showCities(cityResponse.getItems());
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
    }
}