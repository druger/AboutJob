package com.druger.aboutwork.db

import android.util.Log
import com.druger.aboutwork.model.Company
import io.realm.Realm
import io.realm.RealmResults

class RealmHelper {

    companion object {
        const val SCHEMA_VERSION: Long = 1
        const val REALM_NAME = "aboutjob.realm"
    }

    private var realm: Realm = Realm.getDefaultInstance()

    fun saveCompany(company: Company) {
        realm.executeTransactionAsync(
                {realm -> realm.insertOrUpdate(company) },
                {error -> Log.e("Realm: save company", error.message) }
        )
    }

    fun getCompanies() : RealmResults<Company> = realm.where(Company::class.java).findAllAsync()

}