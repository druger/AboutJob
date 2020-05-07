package com.druger.aboutwork.presenters

import android.content.Intent
import android.net.Uri
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.interfaces.view.ReviewView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import timber.log.Timber

open class ReviewPresenter<View : ReviewView> : BasePresenter<View>() {

    private var uri: MutableList<Uri?> = mutableListOf()

    fun getUriImages(data: Intent?) {
        val clipData = data?.clipData
        clipData?.let { cd ->
            for (i in 0 until cd.itemCount) {
                uri.add(cd.getItemAt(i).uri)
            }
        } ?: run { uri.add(data?.data) }
        viewState.showPhotos(uri)
    }

    protected fun uploadPhotos(reviewKey: String?) {
        if (uri.isNotEmpty()) {
            for (uri in uri) {
                uploadPhoto(uri, reviewKey)
            }
        }
    }

    /**
     * upload after deleting photos
     */
    protected fun uploadPhotos(reviewKey: String?, uri: MutableList<Uri?>) {
        if (uri.isNotEmpty()) {
            for (u in uri) {
                uploadPhoto(u, reviewKey)
            }
        }
    }

    private fun uploadPhoto(uri: Uri?, reviewKey: String?) {
        val storageRef = Firebase.storage.reference
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