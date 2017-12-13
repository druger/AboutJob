package com.druger.aboutwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.druger.aboutwork.model.Review;

import java.util.List;

/**
 * Created by druger on 14.12.2017.
 */

public class MyReviewAdapter extends ReviewAdapter {

    public MyReviewAdapter(List<Review> reviews) {
        super(reviews);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }
}
