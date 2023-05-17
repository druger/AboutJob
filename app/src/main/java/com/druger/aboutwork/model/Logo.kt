package com.druger.aboutwork.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Logo: Parcelable {
    @SerializedName("90")
    var logo90: String = ""
    var original: String = ""
}