package com.druger.aboutwork.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
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

    public void getReviews(String companyID, int currentPage) {
        getViewState().showProgressReview();
        dbReference = FirebaseDatabase.getInstance().getReference();
        Query reviewsQuery = INSTANCE.getReviewsForCompany(dbReference, companyID, currentPage);
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
            Query queryUser = INSTANCE.getUser(dbReference, review.getUserId());
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
        getViewState().hideProgressReview();
        getViewState().showReviews(reviews);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getMessage());
    }

    public void removeListeners() {
        if (dbReference != null) {
            dbReference.removeEventListener(this);
            if (valueEventListener != null) {
                dbReference.removeEventListener(valueEventListener);
            }
        }
    }

    public void getCompanyDetail(String companyID) {
        getViewState().showErrorScreen(false);
        getViewState().showProgress(true);
        requestCompanyDetail(companyID);
    }

    private void requestCompanyDetail(String companyID) {
        Disposable request = restApi.getCompany().getCompanyDetail(companyID)
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
