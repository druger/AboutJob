package com.druger.aboutwork.db

import android.util.ArrayMap
import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import java.util.*

/**
 * Created by druger on 16.08.2016.
 */
object FirebaseHelper {
    private const val REVIEWS = "reviews"
    private const val LIKE = "/like"
    private const val DISLIKE = "/dislike"
    private const val LIKES_DISLIKES = "/likesDislikes"
    private const val USERS = "users"
    private const val NAME = "/name"
    private const val COMPANIES = "companies"
    private const val COMMENTS = "comments"
    private const val MESSAGE = "/message"
    private const val REVIEW_ID = "reviewId"
    private const val COMPANY_ID = "companyId"
    private const val ID = "id"
    private const val USER_ID = "userId"
    private const val SLASH = "/"
    private const val LAST_COUNT_REVIEWS = 10

    fun addReview(review: Review) {
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        val map = mapper.convertValue(review, Map::class.java)
        FirebaseDatabase.getInstance().reference.child(REVIEWS).push().setValue(map)
    }

    fun likeOrDislikeReview(review: Review) {
        val updateLike = ArrayMap<String, Any>()
        updateLike[REVIEWS + SLASH + review.firebaseKey + LIKE] = review.like
        updateLike[REVIEWS + SLASH + review.firebaseKey + DISLIKE] = review.dislike
        updateLike[REVIEWS + SLASH + review.firebaseKey + LIKES_DISLIKES] = review.likesDislikes

        FirebaseDatabase.getInstance().reference.updateChildren(updateLike)
    }

    fun changeUserName(name: String, key: String) {
        val updateName = HashMap<String, Any>()
        updateName[USERS + SLASH + key + NAME] = name
        FirebaseDatabase.getInstance().reference.updateChildren(updateName)
    }

    fun addUser(user: User, key: String) {
        FirebaseDatabase.getInstance().reference.child(USERS).child(key).setValue(user)
    }

    fun addCompany(company: Company) {
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        val map = mapper.convertValue(company, Map::class.java)
        FirebaseDatabase.getInstance().reference.child(COMPANIES).child(company.id).setValue(map)
    }

    fun removeReview(id: String) {
        FirebaseDatabase.getInstance().reference.child(REVIEWS + SLASH + id).removeValue()
    }

    fun addComment(comment: Comment) {
        FirebaseDatabase.getInstance().reference.child(COMMENTS).push().setValue(comment)
    }

    fun deleteComment(id: String) {
        FirebaseDatabase.getInstance().reference.child(COMMENTS + SLASH + id).removeValue()
    }

    fun updateComment(id: String, message: String) {
        val updateComment = HashMap<String, Any>()
        updateComment[COMMENTS + SLASH + id + MESSAGE] = message
        FirebaseDatabase.getInstance().reference.updateChildren(updateComment)
    }

    fun updateReview(review: Review) {
        val updateReview = HashMap<String, Any>()
        updateReview[REVIEWS + SLASH + review.firebaseKey] = review
        FirebaseDatabase.getInstance().reference.updateChildren(updateReview)
    }

    fun getComments(reference: DatabaseReference, reviewId: String): Query {
        return reference.child(COMMENTS).orderByChild(REVIEW_ID).equalTo(reviewId)
    }

    fun getReviewsForCompany(dbReference: DatabaseReference, companyId: String): Query {
        return dbReference.child(REVIEWS).orderByChild(COMPANY_ID).equalTo(companyId)
    }

    fun getUser(dbReference: DatabaseReference, userId: String): Query {
        return dbReference.child(USERS).orderByChild(ID).equalTo(userId)
    }

    fun getReviewsById(dbReference: DatabaseReference, userId: String): Query =
        getReviews(dbReference).orderByChild(USER_ID).equalTo(userId)

    fun getLastReviews(dbReference: DatabaseReference): Query =
        getReviews(dbReference).limitToLast(LAST_COUNT_REVIEWS)

    private fun getReviews(dbReference: DatabaseReference): DatabaseReference =
        dbReference.child(REVIEWS)

    fun getReview(dbReference: DatabaseReference, reviewKey: String): Query =
        dbReference.child(REVIEWS).child(reviewKey)

    fun getCompanies(dbReference: DatabaseReference, companyId: String): Query {
        return dbReference.child(COMPANIES).orderByChild(ID).equalTo(companyId)
    }

    fun getCompany(dbReference: DatabaseReference, companyId: String) : Query =
        dbReference.child(COMPANIES).child(companyId)

    fun likeOrDislikeComment(comment: Comment) {
        val updateLike = ArrayMap<String, Any>()
        updateLike[COMMENTS + SLASH + comment.id + LIKE] = comment.like
        updateLike[COMMENTS + SLASH + comment.id + DISLIKE] = comment.dislike
        updateLike[COMMENTS + SLASH + comment.id + LIKES_DISLIKES] = comment.likesDislikes

        FirebaseDatabase.getInstance().reference.updateChildren(updateLike)
    }
}
