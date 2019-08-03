package com.druger.aboutwork

/**
 * Created by druger on 23.06.2017.
 */

object Const {

    object Bundles {

        const val DEBOUNCE_SEARCH = 300

        const val REVIEW_ID = "reviewId"
        const val EDIT_MODE = "editMode"
        const val REVIEW = "review"
        const val NAME = "name"
        const val USER_ID = "userId"
        const val EMAIL = "email"
    }

    object Colors {
        const val RED_500 = "#F44336"
        const val RED_200 = "#EF9A9A"
        const val GRAY_500 = "#9E9E9E"
        const val LIKE = "#3F2B96"
        const val DISLIKE = "#EB5757"
    }

    object ReviewStatus {
        const val NOT_SELECTED_STATUS = -1
        const val WORKING_STATUS = 0
        const val WORKED_STATUS = 1
        const val INTERVIEW_STATUS = 2
    }
}
