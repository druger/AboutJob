package com.druger.aboutwork.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by druger on 05.01.2017.
 */

public class SharedPreferencesHelper {
    private static final String USER_PREF = "User pref";
    private static final String KEY_USERNAME = "userName";

    private static SharedPreferences preferences;

    public static void saveUserName(String name, Context context) {
        preferences = context.getSharedPreferences(USER_PREF, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, name);
        editor.apply();
    }

    public static String getUserName(Context context) {
        preferences = context.getSharedPreferences(USER_PREF, 0);
        return preferences.getString(KEY_USERNAME, "");
    }
}
