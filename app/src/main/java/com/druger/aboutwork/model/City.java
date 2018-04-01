package com.druger.aboutwork.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by druger on 01.04.2018.
 */

public class City {

    @SerializedName("text")
    private String name;

    public String getName() {
        return name;
    }
}
