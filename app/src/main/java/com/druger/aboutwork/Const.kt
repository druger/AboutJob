package com.druger.aboutwork

/**
 * Created by druger on 23.06.2017.
 */

object Const {

    object Bundles {

        const val DEBOUNCE_SEARCH = 500

        const val EDIT_MODE = "editMode"
        const val NAME = "name"
        const val USER_ID = "userId"
        const val EMAIL = "email"
    }

    object Colors {
        const val PURPLE_500 = "#47379F"
        const val PURPLE_100 = "#D1C4E9"
        const val BLACK = "#ff000000"
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
