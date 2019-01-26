package com.druger.aboutwork.model

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Created by druger on 02.03.2017.
 */

class Comment {
    @JsonIgnore
    var id: String? = null
    var userId: String? = null
    var userName: String? = null
    var reviewId: String? = null
    var date: Long = 0
    var message: String? = null
    var like: Int = 0
    var dislike: Int = 0
    var isMyLike: Boolean = false
    var isMyDislike: Boolean = false
    var replyId: String? = null

    constructor() {}

    constructor(message: String, date: Long) {
        this.message = message
        this.date = date
    }
}
