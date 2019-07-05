package com.druger.aboutwork.db

import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.model.Company
import com.druger.aboutwork.model.Review
import com.druger.aboutwork.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

/**
 * Created by druger on 16.08.2016.
 */
object FirebaseHelper {
    private const val REVIEWS = "reviews"
    private const val LIKE = "/like"
    private const val DISLIKE = "/dislike"
    private const val MY_LIKE = "/myLike"
    private const val MY_DISLIKE = "/myDislike"
    private const val USERS = "users"
    private const val NAME = "/name"
    private const val COMPANIES = "companies"
    private const val COMMENTS = "comments"
    private const val MESSAGE = "/message"
    private const val AVATARS = "avatars/"
    private const val AVATAR_NANE = "/avatar.jpg"
    private const val REVIEW_ID = "reviewId"
    private const val COMPANY_ID = "companyId"
    private const val ID = "id"
    private const val USER_ID = "userId"
    private const val SLASH = "/"
    private const val FIRST_COUNT_REVIEWS = 10

    fun addReview(review: Review) {
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        val map = mapper.convertValue(review, Map::class.java)
        FirebaseDatabase.getInstance().reference.child(REVIEWS).push().setValue(map)
    }

    fun likeReview(review: Review) {
        val updateLike = HashMap<String, Any>()
        updateLike[REVIEWS + SLASH + review.firebaseKey + LIKE] = review.like
        updateLike[REVIEWS + SLASH + review.firebaseKey + MY_LIKE] = review.isMyLike

        FirebaseDatabase.getInstance().reference.updateChildren(updateLike)
    }

    fun dislikeReview(review: Review) {
        val updateDislike = HashMap<String, Any>()
        updateDislike[REVIEWS + SLASH + review.firebaseKey + DISLIKE] = review.dislike
        updateDislike[REVIEWS + SLASH + review.firebaseKey + MY_DISLIKE] = review.isMyDislike

        FirebaseDatabase.getInstance().reference.updateChildren(updateDislike)
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

    fun downloadPhoto(storage: FirebaseStorage, userId: String): StorageReference {
        return storage.reference.child(AVATARS + userId + AVATAR_NANE)
    }

    fun savePhoto(storage: FirebaseStorage, userId: String): StorageReference {
        return storage.reference.child(AVATARS + userId + AVATAR_NANE)
    }

    fun getComments(reference: DatabaseReference, reviewId: String): Query {
        return reference.child(COMMENTS).orderByChild(REVIEW_ID).equalTo(reviewId)
    }

    fun getReviewsForCompany(dbReference: DatabaseReference, companyId: String, currentPage: Int): Query {
        return dbReference.child(REVIEWS).orderByChild(COMPANY_ID)
                .equalTo(companyId).limitToFirst(currentPage * FIRST_COUNT_REVIEWS)
    }

    fun getUser(dbReference: DatabaseReference, userId: String): Query {
        return dbReference.child(USERS).orderByChild(ID).equalTo(userId)
    }

    fun getReviews(dbReference: DatabaseReference, userId: String): Query {
        return dbReference.child(REVIEWS).orderByChild(USER_ID).equalTo(userId)
    }

    fun getCompanies(dbReference: DatabaseReference, companyId: String): Query {
        return dbReference.child(COMPANIES).orderByChild(ID).equalTo(companyId)
    }

    fun likeComment(comment: Comment) {
        val updateLike = HashMap<String, Any>()
        updateLike[COMMENTS + SLASH + comment.id + LIKE] = comment.like
        updateLike[COMMENTS + SLASH + comment.id + MY_LIKE] = comment.isMyLike

        FirebaseDatabase.getInstance().reference.updateChildren(updateLike)
    }

    fun dislikeComment(comment: Comment) {
        val updateLike = HashMap<String, Any>()
        updateLike[COMMENTS + SLASH + comment.id + DISLIKE] = comment.dislike
        updateLike[COMMENTS + SLASH + comment.id + MY_DISLIKE] = comment.isMyDislike

        FirebaseDatabase.getInstance().reference.updateChildren(updateLike)
    }
}
