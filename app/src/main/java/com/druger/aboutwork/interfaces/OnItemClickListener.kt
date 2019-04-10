package com.druger.aboutwork.interfaces

/**
 * Created by druger on 29.01.2017.
 */

interface OnItemClickListener<T> {
    fun onClick(item: T, position: Int)
    fun onLongClick(position: Int): Boolean
}
