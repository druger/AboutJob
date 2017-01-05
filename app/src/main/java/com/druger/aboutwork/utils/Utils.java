package com.druger.aboutwork.utils;

import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by druger on 19.08.2016.
 */
public class Utils {

    public static String getDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getNameByEmail(String email) {
        String name = "";
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return email.substring(0, email.indexOf('@'));
        }
        return name;
    }
}
