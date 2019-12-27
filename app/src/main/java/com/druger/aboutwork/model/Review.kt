package com.druger.aboutwork.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Created by druger on 10.08.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Review(var companyId: String? = null,
                  var userId: String? = null,
                  var date: Long = 0) {

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
    var myLike: Boolean = false
    var myDislike: Boolean = false
    @JsonIgnore
    var firebaseKey: String? = null
    var recommended: Boolean? = null


    companion object {
        /**
         * Статусы работника
         */
        const val WORKING = 0
        const val WORKED = 1
        const val INTERVIEW = 2
    }
}
