package com.druger.aboutwork.db;

import com.druger.aboutwork.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by druger on 16.08.2016.
 */
public class DBHelper {
    // TODO: заменить логику на работу с Firebase

    private static List<Review> reviews = new ArrayList<>();

    public static void addReview(Review review) {
        reviews.add(review);
    }

    public static List<Review> getReviews() {
        return reviews;
    }
}
