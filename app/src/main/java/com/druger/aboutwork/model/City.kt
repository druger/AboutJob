package com.druger.aboutwork.model

import com.google.gson.annotations.SerializedName

/**
 * Created by druger on 01.04.2018.
 */

class City(@SerializedName("text") val name: String) {

    override fun toString(): String {
        return name
    }
}