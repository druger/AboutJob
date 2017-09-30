package com.druger.aboutwork.interfaces;

/**
 * Created by druger on 29.01.2017.
 */

public interface OnItemClickListener<T> {
    void onClick(T item, int position);
    boolean onLongClick(int position);
}
