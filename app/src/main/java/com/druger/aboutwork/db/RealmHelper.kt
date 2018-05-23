package com.druger.aboutwork.db

import io.realm.Realm

class RealmHelper {

    companion object {
        const val SCHEMA_VERSION: Long = 1
        const val REALM_NAME = "aboutjob.realm"
    }

    private var realm: Realm = Realm.getDefaultInstance()
}