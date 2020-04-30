package com.druger.aboutwork.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.druger.aboutwork.GlideApp
import com.druger.aboutwork.R
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoAdapter<T>(
    private val uri: MutableList<T?> = mutableListOf(),
    private val canRemovePhoto: Boolean = true
): RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    var isFullScreen = false
    var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoHolder(itemView)
    }

    override fun getItemCount(): Int = uri.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        GlideApp.with(holder.itemView)
            .load(uri[position])
            .centerCrop()
            .placeholder(startProgressDrawable(holder.itemView.context))
            .error(R.drawable.ic_broken_image)
            .into(holder.itemView.ivPhoto)
        if (canRemovePhoto) {
            holder.itemView.ivRemove.setOnClickListener { removePhoto(position) }
        } else {
            holder.itemView.ivRemove.visibility = View.GONE
        }
        holder.itemView.ivPhoto.setOnClickListener { showFullScreen(holder.itemView.context, position, holder.itemView.ivPhoto) }
    }

    private fun startProgressDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 40f
            start()
        }
    }

    fun showFullScreen(context: Context, position: Int, ivPhoto: ImageView?) {
        StfalconImageViewer.Builder<T>(context, uri) { imageView, image ->
                GlideApp.with(context).load(image).into(imageView)
            }
            .withStartPosition(position)
            .withTransitionFrom(ivPhoto)
            .withImageChangeListener {
                currentPosition = it
            }
            .show()

        currentPosition = position
        isFullScreen = true
    }

    class PhotoHolder(view: View): RecyclerView.ViewHolder(view)

    fun addPhotos(uri: List<T?>) {
        this.uri.clear()
        this.uri.addAll(uri)
        notifyDataSetChanged()
    }

    private fun removePhoto(position: Int) {
        uri.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, uri.size)
    }
}