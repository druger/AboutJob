package com.druger.aboutwork.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.druger.aboutwork.GlideApp
import com.druger.aboutwork.R
import com.druger.aboutwork.databinding.ItemPhotoBinding
import com.stfalcon.imageviewer.StfalconImageViewer

class PhotoAdapter<T>(
    private var uri: MutableList<T?> = mutableListOf(),
    private val canRemovePhoto: Boolean = true
) : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    var isFullScreen = false
    var currentPosition = 0
    var wasPhotoRemoved = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoHolder(binding)
    }

    override fun getItemCount(): Int = uri.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        with(holder.binding) {
            GlideApp.with(holder.itemView)
                .load(uri[position])
                .centerCrop()
                .placeholder(startProgressDrawable(holder.itemView.context))
                .error(R.drawable.ic_broken_image)
                .into(ivPhoto)
            if (canRemovePhoto) {
                ivRemove.setOnClickListener { removePhoto(position) }
            } else {
                ivRemove.isVisible = false
            }
            ivPhoto.setOnClickListener {
                showFullScreen(
                    holder.itemView.context,
                    position,
                    ivPhoto
                )
            }
        }
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
            .withImageChangeListener { currentPosition = it }
            .withDismissListener { isFullScreen = false }
            .show()

        currentPosition = position
        isFullScreen = true
    }

    class PhotoHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    fun addPhotos(uri: List<T?>) {
        this.uri.clear()
        this.uri.addAll(uri)
        notifyDataSetChanged()
    }

    private fun removePhoto(position: Int) {
        if (!wasPhotoRemoved) wasPhotoRemoved = true
        uri.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, uri.size)
    }

    fun setUri(uri: MutableList<T?>) {
        this.uri = uri
    }

    fun getItems() = uri
}