package com.druger.aboutwork.presenters

import android.content.Intent
import android.net.Uri
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.ReviewView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import timber.log.Timber

open class ReviewPresenter<View: ReviewView>: BasePresenter<View>() {

    private lateinit var uri: Array<Uri?>

    fun getUriImages(data: Intent?) {
        val clipData = data?.clipData
        clipData?.let { cd ->
            uri = arrayOfNulls(cd.itemCount)
            for (i in uri.indices) {
                uri[i] = cd.getItemAt(i).uri
            }
        } ?: run { uri = arrayOf(data?.data) }
        viewState.showPhotos(uri)
    }

    protected fun uploadPhotos(reviewKey: String?) {
        val storageRef = Firebase.storage.reference
        for (uri in uri) {
            val lastPathSegment = uri?.lastPathSegment
            val photoRef = storageRef.child("${FirebaseHelper.REVIEW_PHOTOS}$reviewKey/$lastPathSegment")
            uri?.let { u ->
                photoRef.putFile(u).apply {
                    addOnSuccessListener { snapshot ->
                        Timber.d(snapshot.metadata.toString())
                    }
                    addOnFailureListener { Timber.e(it) }
                }
            }
        }
    }
}