package com.druger.aboutwork.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Created by druger on 01.04.2018.
 */

@Keep
class City(@SerializedName("text") val name: String) {

    override fun toString(): String {
        return name
    }
}