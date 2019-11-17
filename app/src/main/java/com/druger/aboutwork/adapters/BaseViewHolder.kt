package com.druger.aboutwork.adapters

import android.view.View

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by druger on 13.08.2017.
 */

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected fun <T : View> bindView(@IdRes id: Int): T {
        return itemView.findViewById<View>(id) as T
    }
}
