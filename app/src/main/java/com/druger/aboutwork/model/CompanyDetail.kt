package com.druger.aboutwork.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

/**
 * Created by druger on 04.08.2016.
 */
class CompanyDetail : Parcelable {

    var id: String? = null
    var name: String? = null
    @SerializedName("site_url")
    var site: String? = null
    var description: String? = null
    @SerializedName("logo_urls")
    var logo: Logo? = null
    val area: Area? = null

    constructor() {}

    protected constructor(`in`: Parcel) {
        id = `in`.readString()
        name = `in`.readString()
        site = `in`.readString()
        description = `in`.readString()
        logo = `in`.readParcelable(javaClass.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(site)
        dest.writeString(description)
        dest.writeParcelable(logo, Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
    }

    class Area {
        var name: String? = null
            internal set
    }

    companion object CREATOR : Parcelable.Creator<CompanyDetail> {
        override fun createFromParcel(parcel: Parcel): CompanyDetail {
            return CompanyDetail(parcel)
        }

        override fun newArray(size: Int): Array<CompanyDetail?> {
            return arrayOfNulls(size)
        }
    }
}
