package com.druger.aboutwork.adapters

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.druger.aboutwork.model.Review

/**
 * Created by druger on 14.12.2017.
 */

class MyReviewAdapter(reviews: MutableList<Review>) : ReviewAdapter(reviews) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
    }
}
