package com.druger.aboutwork.db;

import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by druger on 16.08.2016.
 */
public class FirebaseHelper {

    public static void addReview(Review review) {
        ObjectMapper mapper = new ObjectMapper();;
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Map<String, Object> map = mapper.convertValue(review, Map.class);
        FirebaseDatabase.getInstance().getReference().child("reviews").push().setValue(map);
    }

    public static void setLike(Review review) {
        Map<String, Object> updateLike = new HashMap<>();
        updateLike.put("reviews/" + review.getFirebaseKey() + "/like", review.getLike());
        updateLike.put("reviews/" + review.getFirebaseKey() + "/myLike", review.isMyLike());

        FirebaseDatabase.getInstance().getReference().updateChildren(updateLike);
    }

    public static void setDislike(Review review) {
        Map<String, Object> updateDislike = new HashMap<>();
        updateDislike.put("reviews/" + review.getFirebaseKey() + "/dislike", review.getDislike());
        updateDislike.put("reviews/" + review.getFirebaseKey() + "/myDislike", review.isMyDislike());

        FirebaseDatabase.getInstance().getReference().updateChildren(updateDislike);
    }

    public static void changeUserName(String name, String key) {
        Map<String, Object> updateName = new HashMap<>();
        updateName.put("users/" + key + "/name", name);
        FirebaseDatabase.getInstance().getReference().updateChildren(updateName);
    }

    public static void addUser(User user, String key) {
        FirebaseDatabase.getInstance().getReference().child("users").child(key).setValue(user);
    }

    public static void addCompany(String key, String name) {
        Company company = new Company(key, name);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Map<String, Object> map = mapper.convertValue(company, Map.class);
        FirebaseDatabase.getInstance().getReference().child("companies").child(key).setValue(map);
    }

    public static void removeReview(String id) {
        FirebaseDatabase.getInstance().getReference().child("reviews/" + id).removeValue();
    }

    public static void addComment(Comment comment) {
        FirebaseDatabase.getInstance().getReference().child("comments").push().setValue(comment);
    }

    public static void deleteComment(String id) {
        FirebaseDatabase.getInstance().getReference().child("comments/" + id).removeValue();
    }

    public static void updateComment(String id, String message) {
        Map<String, Object> updateComment = new HashMap<>();
        updateComment.put("comments/" + id + "/message", message);
        FirebaseDatabase.getInstance().getReference().updateChildren(updateComment);
    }
}
