package com.druger.aboutwork.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.Parcelize

/**
 * Created by druger on 10.08.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class Review(val companyId: String,
             val userId: String,
             val date: Long = 0) : Parcelable {

    @JsonIgnore
    var name: String? = null
    var pluses: String? = null
    var minuses: String? = null
    var markCompany: MarkCompany? = null
    var status: Int = 0
    var city: String? = null
    var position: String? = null
    var employmentDate: Long = 0
    var dismissalDate: Long = 0
    var interviewDate: Long = 0
    var like: Int = 0
    var dislike: Int = 0
    var isMyLike: Boolean = false
    var isMyDislike: Boolean = false
    @JsonIgnore
    var firebaseKey: String? = null

    companion object {
        /**
         * Статусы работника
         */
        const val WORKING = 0
        const val WORKED = 1
        const val INTERVIEW = 2
    }
}
