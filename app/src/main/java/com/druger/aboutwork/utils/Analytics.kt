package com.druger.aboutwork.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(val firebase: FirebaseAnalytics) {

    @JvmOverloads
    fun logEvent(event: String, key: String? = null, value: String? = null) {
        val bundle = Bundle().apply {
            putString(key, value)
        }
        firebase.logEvent(event, bundle)
    }

    companion object {
        const val EMPLOYMENT_DATE_CLICK = "employment_date_click"
        const val DISMISSAL_DATE_CLICK = "dismissal_date_click"
        const val ADD_REVIEW_CLICK = "add_review_click"
        const val CLOSE_ADD_REVIEW_CLICK = "close_add_review_click"
        const val INTERVIEW_STATUS_CLICK = "interview_status_click"
        const val WORKING_STATUS_CLICK = "working_status_click"
        const val WORKED_STATUS_CLICK = "worked_status_click"

        const val LONG_CLICK_MY_REVIEW = "long_click_my_review"
        const val SWIPE_MY_REVIEW = "swipe_my_review"

        const val LONG_CLICK_MY_COMMENT = "long_click_my_comment"
        const val ADD_COMMENT = "add_comment"
        const val UPDATE_COMMENT = "update_comment"
        const val DELETE_COMMENT = "delete_comment"

        const val REMOVE_ACCOUNT = "remove_account"
    }
}