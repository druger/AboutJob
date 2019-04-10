package com.druger.aboutwork.model

import android.os.Parcel
import android.os.Parcelable

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Created by druger on 10.08.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Review : Parcelable {

    var companyId: String? = null
    var userId: String? = null
    @JsonIgnore
    var name: String? = null
    var date: Long = 0
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

    constructor() {}

    constructor(companyId: String, userId: String, date: Long) {
        this.companyId = companyId
        this.userId = userId
        this.date = date
    }

    protected constructor(`in`: Parcel) {
        companyId = `in`.readString()
        userId = `in`.readString()
        name = `in`.readString()
        date = `in`.readLong()
        pluses = `in`.readString()
        minuses = `in`.readString()
        status = `in`.readInt()
        city = `in`.readString()
        position = `in`.readString()
        employmentDate = `in`.readLong()
        dismissalDate = `in`.readLong()
        interviewDate = `in`.readLong()
        like = `in`.readInt()
        dislike = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(companyId)
        dest.writeString(userId)
        dest.writeString(name)
        dest.writeLong(date)
        dest.writeString(pluses)
        dest.writeString(minuses)
        dest.writeInt(status)
        dest.writeString(city)
        dest.writeString(position)
        dest.writeLong(employmentDate)
        dest.writeLong(dismissalDate)
        dest.writeLong(interviewDate)
        dest.writeInt(like)
        dest.writeInt(dislike)
    }

    companion object {

        /**
         * Статусы работника
         */
        const val WORKING = 0
        const val WORKED = 1
        const val INTERVIEW = 2

        @JvmField
        val CREATOR: Parcelable.Creator<Review> = object : Parcelable.Creator<Review> {
            override fun createFromParcel(`in`: Parcel): Review {
                return Review(`in`)
            }

            override fun newArray(size: Int): Array<Review?> {
                return arrayOfNulls(size)
            }
        }
    }
}
