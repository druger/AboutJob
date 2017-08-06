package com.druger.aboutwork.utils;

import android.content.SharedPreferences;

/**
 * Created by druger on 05.01.2017.
 */

public class PreferencesHelper {
    private static final String KEY_USERNAME = "userName";

    private SharedPreferences preferences;

    public PreferencesHelper(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void saveUserName(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, name);
        editor.apply();
    }

    public String getUserName() {
        return preferences.getString(KEY_USERNAME, "");
    }
}
