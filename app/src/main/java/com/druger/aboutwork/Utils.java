package com.druger.aboutwork;

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
}
