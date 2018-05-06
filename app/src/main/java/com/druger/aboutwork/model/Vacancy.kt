package com.druger.aboutwork.model

import com.google.gson.annotations.SerializedName

class Vacancy(@SerializedName("text") val name: String){
    override fun toString(): String {
        return name
    }
}