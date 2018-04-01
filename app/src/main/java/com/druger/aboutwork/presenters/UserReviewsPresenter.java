package com.druger.aboutwork.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.view.UserReviews;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.druger.aboutwork.db.FirebaseHelper.getCompanies;
import static com.druger.aboutwork.db.FirebaseHelper.getReviews;

/**
 * Created by druger on 31.01.2018.
 */

@InjectViewState
public class UserReviewsPresenter extends BasePresenter<UserReviews> implements ValueEventListener {

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference dbReference;
    private ValueEventListener valueEventListener;
    private ValueEventListener nameEventListener;

    private List<Review> reviews;

    public UserReviewsPresenter() {
        storage = FirebaseStorage.getInstance();
        reviews = new ArrayList<>();
    }

    public void downloadPhoto(String userId) {
        storageRef = FirebaseHelper.downloadPhoto(storage, userId);
        getViewState().showPhoto(storageRef);
    }

    public void fetchReviews(String userId, int currentPage) {
        dbReference = FirebaseDatabase.getInstance().getReference();

        Query reviewsQuery = getReviews(dbReference, userId, currentPage);
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
            Query queryCompanies = getCompanies(dbReference, review.getCompanyId());
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
            queryCompanies.addValueEventListener(valueEventListener);
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
        if (nameEventListener != null) {
            dbReference.removeEventListener(nameEventListener);
        }
    }

    public void getUserName(String id) {
        getName(id);
    }

    private void getName(String id) {
        Query queryUser = FirebaseHelper.getUser(dbReference, id);
        nameEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            getViewState().showName(user.getName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        queryUser.addValueEventListener(nameEventListener);
    }
}