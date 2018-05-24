package com.druger.aboutwork.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

/**
 * Created by druger on 24.07.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Company(var id: String = "", var name: String = "") : RealmObject() {

    @JsonIgnore
    @SerializedName("logo_urls")
    var logo: Logo? = null

    inner class Logo {
        @SerializedName("90")
        var logo90: String = ""
        @SerializedName("240")
        var logo240: String = ""
        var original: String = ""
    }
}
