package com.druger.aboutwork.db

import com.druger.aboutwork.model.realm.CompanyRealm
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import timber.log.Timber

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
                { error -> Timber.tag("Realm: save company").e(error) }
        )
    }

    fun getCompanies(): RealmResults<CompanyRealm> = realm.where(CompanyRealm::class.java)
            .sort(DATE_COLUMN, Sort.DESCENDING).findAllAsync()

    fun deleteAllData() {
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }
}