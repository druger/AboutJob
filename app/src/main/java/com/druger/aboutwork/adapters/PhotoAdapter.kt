package com.druger.aboutwork.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.druger.aboutwork.R
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoAdapter(private val uri: MutableList<Uri?> = mutableListOf()): RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoHolder(itemView)
    }

    override fun getItemCount(): Int = uri.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        Glide.with(holder.itemView).load(uri[position]).centerCrop().into(holder.itemView.ivPhoto)
        holder.itemView.ivRemove.setOnClickListener { removePhoto(position) }
    }

    class PhotoHolder(view: View): RecyclerView.ViewHolder(view)

    fun addPhotos(uri: Array<Uri?>) {
        this.uri.addAll(uri)
        notifyDataSetChanged()
    }

    private fun removePhoto(position: Int) {
        uri.removeAt(position)
        notifyItemRemoved(position)
    }
}