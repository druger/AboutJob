package com.druger.aboutwork.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by druger on 03.06.2018.
 */
open class CompanyRealm(@PrimaryKey var id: String = "",
                        var name: String = "",
                        var logo: String = "") : RealmObject() {

    var date: Long = 0
}
