package com.druger.aboutwork.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * Created by druger on 24.07.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class Company(@PrimaryKey var id: String = "", var name: String = "") : RealmObject() {

    @Ignore
    @JsonIgnore
    @SerializedName("logo_urls")
    var logo: Logo? = null

    var date: Long = 0

    inner class Logo {
        @SerializedName("90")
        var logo90: String = ""
        @SerializedName("240")
        var logo240: String = ""
        var original: String = ""
    }
}
