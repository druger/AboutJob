package com.druger.aboutwork.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Logo: Parcelable {
    @SerializedName("90")
    var logo90: String = ""
    var original: String = ""
}