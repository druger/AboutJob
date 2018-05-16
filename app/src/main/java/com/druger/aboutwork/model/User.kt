package com.druger.aboutwork.model

/**
 * Created by druger on 28.12.2016.
 */

class User {
    var id: String? = null
    var name: String? = null

    constructor() {}

    constructor(id: String, name: String) {
        this.id = id
        this.name = name
    }
}
