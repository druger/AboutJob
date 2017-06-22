package com.druger.aboutwork.db;

import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by druger on 16.08.2016.
 */
public class FirebaseHelper {
    private static final String REVIEWS = "reviews";
    private static final String LIKE = "/like";
    private static final String DISLIKE = "/dislike";
    private static final String MY_LIKE = "/myLike";
    private static final String MY_DISLIKE = "/myDisLike";
    private static final String USERS = "users";
    private static final String NAME = "/name";
    private static final String COMPANIES = "companies";
    private static final String COMMENTS = "comments";
    private static final String MESSAGE = "/message";
    private static final String AVATARS = "avatars/";
    private static final String AVATAR_NANE = "/avatar.jpg";
    private static final String REVIEW_ID = "reviewId";
    private static final String COMPANY_ID = "companyId";
    private static final String ID = "id";
    private static final String USER_ID = "userId";

    public static void addReview(Review review) {
        ObjectMapper mapper = new ObjectMapper();;
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Map<String, Object> map = mapper.convertValue(review, Map.class);
        FirebaseDatabase.getInstance().getReference().child(REVIEWS).push().setValue(map);
    }

    public static void setLike(Review review) {
        Map<String, Object> updateLike = new HashMap<>();
        updateLike.put(REVIEWS + "/" + review.getFirebaseKey() + LIKE, review.getLike());
        updateLike.put(REVIEWS + "/" + review.getFirebaseKey() + MY_LIKE, review.isMyLike());

        FirebaseDatabase.getInstance().getReference().updateChildren(updateLike);
    }

    public static void setDislike(Review review) {
        Map<String, Object> updateDislike = new HashMap<>();
        updateDislike.put(REVIEWS + "/" + review.getFirebaseKey() + DISLIKE, review.getDislike());
        updateDislike.put(REVIEWS + "/" + review.getFirebaseKey() + MY_DISLIKE, review.isMyDislike());

        FirebaseDatabase.getInstance().getReference().updateChildren(updateDislike);
    }

    public static void changeUserName(String name, String key) {
        Map<String, Object> updateName = new HashMap<>();
        updateName.put(USERS + "/" + key + NAME, name);
        FirebaseDatabase.getInstance().getReference().updateChildren(updateName);
    }

    public static void addUser(User user, String key) {
        FirebaseDatabase.getInstance().getReference().child(USERS).child(key).setValue(user);
    }

    public static void addCompany(String key, String name) {
        Company company = new Company(key, name);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Map<String, Object> map = mapper.convertValue(company, Map.class);
        FirebaseDatabase.getInstance().getReference().child(COMPANIES).child(key).setValue(map);
    }

    public static void removeReview(String id) {
        FirebaseDatabase.getInstance().getReference().child(REVIEWS + "/" + id).removeValue();
    }

    public static void addComment(Comment comment) {
        FirebaseDatabase.getInstance().getReference().child(COMMENTS).push().setValue(comment);
    }

    public static void deleteComment(String id) {
        FirebaseDatabase.getInstance().getReference().child(COMMENTS + "/" + id).removeValue();
    }

    public static void updateComment(String id, String message) {
        Map<String, Object> updateComment = new HashMap<>();
        updateComment.put(COMMENTS + "/" + id + MESSAGE, message);
        FirebaseDatabase.getInstance().getReference().updateChildren(updateComment);
    }

    public static void updateReview(Review review) {
        Map<String, Object> updateReview = new HashMap<>();
        updateReview.put(REVIEWS + "/" + review.getFirebaseKey(), review);
        FirebaseDatabase.getInstance().getReference().updateChildren(updateReview);
    }

    public static StorageReference downloadPhoto(FirebaseStorage storage, String userId) {
        return storage.getReference().child(AVATARS + userId + AVATAR_NANE);
    }

    public static StorageReference savePhoto(FirebaseStorage storage, String userId) {
        return storage.getReference().child(AVATARS + userId + AVATAR_NANE);
    }

    public static Query getComments(DatabaseReference reference, String reviewId) {
        return reference.child(COMMENTS).orderByChild(REVIEW_ID).equalTo(reviewId);
    }

    public static Query getReviewsForCompany(DatabaseReference dbReference, String companyId) {
        return dbReference.child(REVIEWS).orderByChild(COMPANY_ID).equalTo(companyId);
    }

    public static Query getUser(DatabaseReference dbReference, String userId) {
        return dbReference.child(USERS).orderByChild(ID).equalTo(userId);
    }

    public static Query getMyReviews(DatabaseReference dbReference, String userId) {
        return dbReference.child(REVIEWS).orderByChild(USER_ID).equalTo(userId);
    }

    public static Query getCompanies(DatabaseReference dbReference, String companyId) {
        return dbReference.child(COMPANIES).orderByChild(ID).equalTo(companyId);
    }
}
