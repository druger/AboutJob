package com.druger.aboutwork.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.druger.aboutwork.interfaces.OnItemClickListener
import java.util.*

/**
 * Created by druger on 13.08.2017.
 */

abstract class BaseRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private var items: MutableList<T> = ArrayList()
    protected var clickListener: OnItemClickListener<T>? = null

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    fun add(item: T, position: Int) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        notifyItemRemoved(position)
        items.removeAt(position)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addItems(items: List<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    protected fun inflate(layoutID: Int, viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(layoutID, viewGroup, false)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener<T>) {
        this.clickListener = clickListener
    }
}
