package com.druger.aboutwork.presenters;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.MyReviewsView;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
public class MyReviewsPresenter extends MvpPresenter<MyReviewsView> implements ValueEventListener {

    private DatabaseReference dbReference;
    private ValueEventListener valueEventListener;

    private List<Review> reviews;

    private ItemTouchHelper touchHelper;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private boolean itemSwipe = true;

    public MyReviewsPresenter() {
        reviews = new ArrayList<>();
    }

    public void fetchReviews(String userId) {
        dbReference = FirebaseDatabase.getInstance().getReference();

        Query reviewsQuery = dbReference.child("reviews").orderByChild("userId").equalTo(userId);
        reviewsQuery.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        fetchReviews(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void fetchReviews(DataSnapshot dataSnapshot) {
        reviews.clear();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            final Review review = snapshot.getValue(Review.class);
            Query queryByCompanyId = dbReference.child("companies").orderByChild("id").equalTo(review.getCompanyId());
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Company company = data.getValue(Company.class);
                            review.setName(company.getName());
                            getViewState().notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            queryByCompanyId.addValueEventListener(valueEventListener);
            review.setFirebaseKey(snapshot.getKey());
            reviews.add(review);
        }
        getViewState().showReviews(reviews);
    }

    public void removeListeners() {
        dbReference.removeEventListener(this);
        if (valueEventListener != null) {
            dbReference.removeEventListener(valueEventListener);
        }
    }

    public Review getReview(int position) {
        return reviews.get(position);
    }

    public void addReview(int position, Review review) {
        reviews.add(position, review);
        addToFirebase(review);
    }

    public void removeReview(int position) {
        FirebaseHelper.removeReview(reviews.remove(position).getFirebaseKey());
    }

    public void addDeletedReviews(List<Review> deletedReviews) {
        reviews.addAll(deletedReviews);
    }

    public void addToFirebase(Review review) {
        FirebaseHelper.addReview(review);
    }
}
