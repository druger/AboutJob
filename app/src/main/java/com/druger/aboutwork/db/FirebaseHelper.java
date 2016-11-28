package com.druger.aboutwork.db;

import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by druger on 16.08.2016.
 */
public class FirebaseHelper {
    // TODO: заменить логику на работу с Firebase

    private static List<Review> reviews = new ArrayList<>();

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

    public static List<Review> getReviews() {
        return reviews;
    }

    // Fake data
    static {
        Review review = new Review(1, "1", "Guf", 123456);
        MarkCompany markCompany = new MarkCompany("1", 1);
        markCompany.setSocialPackage(3f);
        markCompany.setCollective(2f);
        markCompany.setCareer(4f);
        markCompany.setChief(1f);
        markCompany.setSalary(2f);
        markCompany.setWorkplace(4f);
        review.setMarkCompany(markCompany);
        review.setLike(1);
        review.setCity("Moscow");
        review.setMyLike(true);

        Review review2 = new Review(2, "2", "Slim", 123456789);
        MarkCompany markCompany2 = new MarkCompany("2", 2);
        markCompany2.setSocialPackage(1f);
        markCompany2.setCollective(2f);
        markCompany2.setCareer(2f);
        markCompany2.setChief(1f);
        markCompany2.setSalary(2f);
        markCompany2.setWorkplace(3f);
        review2.setMarkCompany(markCompany2);
        review2.setLike(2);
        review2.setDislike(1);
        review2.setCity("Moscow");
        review2.setMyDislike(true);

        Review review3 = new Review(3, "3", "Oxxxymiron", 123456789);
        MarkCompany markCompany3 = new MarkCompany("3", 3);
        markCompany3.setSocialPackage(5f);
        markCompany3.setCollective(4f);
        markCompany3.setCareer(3f);
        markCompany3.setChief(2f);
        markCompany3.setSalary(3f);
        markCompany3.setWorkplace(4f);
        review3.setMarkCompany(markCompany3);
        review3.setLike(3);
        review3.setDislike(2);
        review3.setCity("Piter");

        reviews.add(review);
        reviews.add(review2);
        reviews.add(review3);
    }
}
