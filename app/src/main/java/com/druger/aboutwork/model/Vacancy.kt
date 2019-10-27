package com.druger.aboutwork.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class Vacancy(@SerializedName("text") val name: String){
    override fun toString(): String {
        return name
    }
}