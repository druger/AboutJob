package com.druger.aboutwork.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.druger.aboutwork.R
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoAdapter<T>(
    private val uri: MutableList<T?> = mutableListOf(),
    private val canRemovePhoto: Boolean = true
): RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoHolder(itemView)
    }

    override fun getItemCount(): Int = uri.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        Glide.with(holder.itemView).load(uri[position]).centerCrop().into(holder.itemView.ivPhoto)
        if (canRemovePhoto) {
            holder.itemView.ivRemove.setOnClickListener { removePhoto(position) }
        } else {
            holder.itemView.ivRemove.visibility = View.GONE
        }
        holder.itemView.ivPhoto.setOnClickListener { showFullScreen(holder.itemView.context, position, holder.itemView.ivPhoto) }
    }

    private fun showFullScreen(context: Context, position: Int, ivPhoto: ImageView) {
        StfalconImageViewer.Builder<T>(context, uri) { imageView, image ->
                Glide.with(context).load(image).into(imageView)
            }
            .withStartPosition(position)
            .withTransitionFrom(ivPhoto)
            .show()
    }

    class PhotoHolder(view: View): RecyclerView.ViewHolder(view)

    fun addPhotos(uri: Array<T?>) {
        this.uri.addAll(uri)
        notifyDataSetChanged()
    }

    private fun removePhoto(position: Int) {
        uri.removeAt(position)
        notifyItemRemoved(position)
    }
}