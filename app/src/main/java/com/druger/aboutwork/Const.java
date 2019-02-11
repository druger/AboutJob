package com.druger.aboutwork;

/**
 * Created by druger on 23.06.2017.
 */

public final class Const {

    private Const() {
    }

    public static final class Bundles {
        private Bundles() {
        }

        public static final int DEBOUNCE_SEARCH = 300;

        public static final String REVIEW_ID = "reviewId";
        public static final String EDIT_MODE = "editMode";
        public static final String REVIEW = "review";
        public static final String NAME = "name";
        public static final String COMPANY_DETAIL = "companyDetail";
        public static final String COMPANY_ID = "companyID";
        public static final String USER_ID = "userId";
        public static final String EMAIL = "email";
    }

    public static final class Colors {
        private Colors() {
        }
        public static final String RED_500 = "#F44336";
        public static final String RED_200 = "#EF9A9A";
        public static final String GRAY_500 = "#9E9E9E";
        public static final String LIKE = "#3F2B96";
        public static final String DISLIKE = "#EB5757";
    }

    public static final class ReviewStatus {
        public static final int NOT_SELECTED_STATUS = -1;
        public static final int WORKING_STATUS = 0;
        public static final int WORKED_STATUS = 1;
        public static final int INTERVIEW_STATUS = 2;
    }
}
