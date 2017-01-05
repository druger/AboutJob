package com.druger.aboutwork.db;

import com.druger.aboutwork.model.Review;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by druger on 16.08.2016.
 */
public class FirebaseHelper {
    private DatabaseReference dbReference;

    public FirebaseHelper() {
        dbReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addReview(Review review) {
        ObjectMapper mapper = new ObjectMapper();;
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Map<String, Object> map = mapper.convertValue(review, Map.class);
        dbReference.child("reviews").push().setValue(map);
    }

    public void setLike(Review review) {
        Map<String, Object> updateLike = new HashMap<>();
        updateLike.put("reviews/" + review.getFirebaseKey() + "/like", review.getLike());
        updateLike.put("reviews/" + review.getFirebaseKey() + "/myLike", review.isMyLike());

        dbReference.updateChildren(updateLike);
    }

    public void setDislike(Review review) {
        Map<String, Object> updateDislike = new HashMap<>();
        updateDislike.put("reviews/" + review.getFirebaseKey() + "/dislike", review.getDislike());
        updateDislike.put("reviews/" + review.getFirebaseKey() + "/myDislike", review.isMyDislike());

        dbReference.updateChildren(updateDislike);
    }
}
