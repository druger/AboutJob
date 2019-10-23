package com.druger.aboutwork.interfaces.view

import com.druger.aboutwork.model.Review
import com.google.firebase.storage.StorageReference
import moxy.MvpView

/**
 * Created by druger on 31.01.2018.
 */

interface UserReviews : MvpView {

    fun showPhoto(storageRef: StorageReference)

    fun notifyDataSetChanged()

    fun showReviews(reviews: List<Review>)

    fun showName(name: String)
}
