package com.druger.aboutwork.rest.models

import com.druger.aboutwork.model.Company

/**
 * Created by druger on 30.07.2016.
 */
class CompanyResponse {
    var page: Int = 0
    var pages: Int = 0
    var found: Int = 0
    var items: List<Company>? = null

    companion object {

        val PER_PAGE = 20
    }
}
