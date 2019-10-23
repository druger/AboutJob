package com.druger.aboutwork.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.druger.aboutwork.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by druger on 13.08.2017.
 */

public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected List<T> items;
    protected OnItemClickListener<T> clickListener;

    public BaseRecyclerViewAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    public void add(T item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        notifyItemRemoved(position);
        items.remove(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    protected View inflate(int layoutID, ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(layoutID, viewGroup, false);
    }

    public void setOnItemClickListener(OnItemClickListener<T> clickListener) {
        this.clickListener = clickListener;
    }
}
