package com.druger.aboutwork.states

import com.druger.aboutwork.model.Company

data class CompaniesState(
    val companies: List<Company>,
    val pages: Int
)
