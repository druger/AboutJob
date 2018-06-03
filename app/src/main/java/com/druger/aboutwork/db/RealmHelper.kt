package com.druger.aboutwork.db

import android.util.Log
import com.druger.aboutwork.model.realm.CompanyRealm
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class RealmHelper {

    companion object {
        const val SCHEMA_VERSION: Long = 2
        const val REALM_NAME = "aboutjob.realm"

        const val DATE_COLUMN = "date"
    }

    private var realm: Realm = Realm.getDefaultInstance()

    fun saveCompany(company: CompanyRealm) {
        realm.executeTransactionAsync(
                { realm ->
                    company.date = System.currentTimeMillis()
                    realm.insertOrUpdate(company)
                },
                { error -> Log.e("Realm: save company", error.message) }
        )
    }

    fun getCompanies(): RealmResults<CompanyRealm> = realm.where(CompanyRealm::class.java)
            .sort(DATE_COLUMN, Sort.DESCENDING).findAllAsync()
}