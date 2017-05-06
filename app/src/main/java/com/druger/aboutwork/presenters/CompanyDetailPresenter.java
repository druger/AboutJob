package com.druger.aboutwork.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.interfaces.view.CompanyDetailView;
import com.druger.aboutwork.model.CompanyDetail;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by druger on 01.05.2017.
 */

@InjectViewState
public class CompanyDetailPresenter extends MvpPresenter<CompanyDetailView>
        implements ValueEventListener {

    private DatabaseReference dbReference;
    private ValueEventListener valueEventListener;

    private List<Review> reviews = new ArrayList<>();

    public void downDropClick() {
        getViewState().showDescription();
    }

    public void upDropClick() {
        getViewState().hideDescription();
    }

    public void setReviews(CompanyDetail detail) {
        dbReference = FirebaseDatabase.getInstance().getReference();
        Query reviewsQuery = dbReference.child("reviews").orderByChild("companyId").equalTo(detail.getId());
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
            Query queryUserId = dbReference.child("users").orderByChild("id").equalTo(review.getUserId());
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
            queryUserId.addValueEventListener(valueEventListener);
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
            mRating = MarkCompany.roundMark(sum / reviews.size(), 2);
        }
        getViewState().showRating(mRating);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void removeListeners() {
        dbReference.removeEventListener(this);
        if (valueEventListener != null) {
            dbReference.removeEventListener(valueEventListener);
        }
    }
}
