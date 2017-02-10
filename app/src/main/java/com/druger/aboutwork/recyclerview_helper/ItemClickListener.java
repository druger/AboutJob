package com.druger.aboutwork.recyclerview_helper;

import android.view.View;

/**
 * Created by druger on 29.01.2017.
 */

public interface ItemClickListener {
    void onClick(View view, int position);
    boolean onLongClick(View view, int position);
}
