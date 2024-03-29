package com.druger.aboutwork.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName

/**
 * Created by druger on 24.07.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Company(var id: String = "", var name: String = "") {
    @JsonIgnore
    @SerializedName("logo_urls")
    var logo: Logo? = null
    @JsonIgnore
    var city = ""
}
