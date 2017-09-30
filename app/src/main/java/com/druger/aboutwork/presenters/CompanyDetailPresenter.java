package com.druger.aboutwork.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.model.User;
import com.druger.aboutwork.rest.RestApi;
import com.druger.aboutwork.utils.rx.RxUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

import static com.druger.aboutwork.db.FirebaseHelper.getReviewsForCompany;
import static com.druger.aboutwork.db.FirebaseHelper.getUser;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompanyDetailPresenter extends BasePresenter<CompanyDetailView>
        implements ValueEventListener {

    @Inject
    public CompanyDetailPresenter(RestApi restApi) {
        this.restApi = restApi;
    }


    private DatabaseReference dbReference;
    private ValueEventListener valueEventListener;

    private List<Review> reviews = new ArrayList<>();

    public void downDropClick() {
        getViewState().showDescription();
    }

    public void upDropClick() {
        getViewState().hideDescription();
    }

    public void setReviews(String companyID) {
        dbReference = FirebaseDatabase.getInstance().getReference();
        Query reviewsQuery = getReviewsForCompany(dbReference, companyID);
        reviewsQuery.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        fetchReviews(dataSnapshot);
    }

    private void fetchReviews(DataSnapshot dataSnapshot) {
        reviews.clear();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            final Review review = snapshot.getValue(Review.class);
            Query queryUser = getUser(dbReference, review.getUserId());
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            User user = data.getValue(User.class);
                            review.setName(user.getName());
                            getViewState().updateAdapter();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            queryUser.addValueEventListener(valueEventListener);
            review.setFirebaseKey(snapshot.getKey());
            reviews.add(review);
        }
        getViewState().showCountReviews(reviews.size());
        setRating();
        getViewState().showReviews(reviews);
    }

    private void setRating() {
        float sum = 0;
        float mRating = 0;

        if (!reviews.isEmpty()) {
            for (Review review : reviews) {
                MarkCompany markCompany = review.getMarkCompany();
                sum += markCompany != null ? markCompany.getAverageMark() : 0;
            }
            mRating = MarkCompany.roundMark(sum / reviews.size());
        }
        getViewState().showRating(mRating);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getMessage());
    }

    public void removeListeners() {
        dbReference.removeEventListener(this);
        if (valueEventListener != null) {
            dbReference.removeEventListener(valueEventListener);
        }
    }

    public void getCompanyDetail(String companyID) {
        getViewState().showErrorScreen(false);
        getViewState().showProgress(true);
        requestCompanyDetail(companyID);
    }

    private void requestCompanyDetail(String companyID) {
        Disposable request = restApi.company.getCompanyDetail(companyID)
                .compose(RxUtils.httpSchedulers())
                .subscribe(this::successGetCompanyDetails, this::handleError);

        unSubscribeOnDestroy(request);
    }

    private void successGetCompanyDetails(CompanyDetail companyDetail) {
        getViewState().showProgress(false);
        getViewState().showCompanyDetail(companyDetail);
    }

    @Override
    protected void handleError(Throwable throwable) {
        super.handleError(throwable);
        getViewState().showProgress(false);
        getViewState().showErrorScreen(true);
    }
}
