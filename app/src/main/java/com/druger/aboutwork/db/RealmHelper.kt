package com.druger.aboutwork.db

import com.druger.aboutwork.model.Company
import io.realm.Realm

class RealmHelper {

    companion object {
        const val SCHEMA_VERSION: Long = 1
        const val REALM_NAME = "aboutjob.realm"
    }

    private var realm: Realm = Realm.getDefaultInstance()

    fun saveCompany(company: Company) {
        realm.beginTransaction()
        realm.insert(company)
        realm.commitTransaction()
    }

    fun getCompanies() : List<Company> = realm.where(Company::class.java).findAllAsync()

}