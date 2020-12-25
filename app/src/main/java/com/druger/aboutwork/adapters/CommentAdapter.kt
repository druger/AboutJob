package com.druger.aboutwork.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.collection.ArrayMap
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.druger.aboutwork.R
import com.druger.aboutwork.db.FirebaseHelper
import com.druger.aboutwork.model.Comment
import com.druger.aboutwork.utils.Utils
import com.google.firebase.auth.FirebaseUser


/**
 * Created by druger on 04.03.2017.
 */

class CommentAdapter(private val user: FirebaseUser?) : BaseRecyclerViewAdapter<Comment, CommentAdapter.CommentVH>() {

    private var nameClickListener: OnNameClickListener? = null
    private var likesDislikes: MutableMap<String, Boolean>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        val itemView = inflate(R.layout.item_comment, parent)
        return CommentVH(itemView)
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        val comment = getItem(position)
        holder.tvUserName.text = comment.userName
        holder.tvComment.text = comment.message
        holder.tvDate.text = Utils.getDate(comment.date)
        holder.tvCountLikes.text = comment.like.toString()
        holder.tvCountDislikes.text = comment.dislike.toString()

        holder.itemView.setOnLongClickListener { longItemClick(comment, holder) }
        holder.ivLike.setOnClickListener { likeClick(holder, comment) }
        holder.ivDislike.setOnClickListener { dislikeClick(holder, comment) }
        holder.tvUserName.setOnClickListener { nameClickListener?.onClick(comment) }

        setColorLikeDislike(comment, holder)
    }

    private fun setColorLikeDislike(comment: Comment, holder: CommentVH) {
        holder.ivLike.setColorFilter(ResourcesCompat.getColor(
            holder.itemView.context.resources, R.color.like_disable, null))
        holder.ivDislike.setColorFilter(ResourcesCompat.getColor(
            holder.itemView.context.resources, R.color.like_disable, null))
        likesDislikes = comment.likesDislikes
        likesDislikes?.let { likes ->
            likes[user?.uid]?.let { myLike ->
                if (myLike) {
                    holder.ivLike.setColorFilter(
                        ResourcesCompat.getColor(
                            holder.itemView.context.resources, R.color.like, null
                        ))
                } else {
                    holder.ivDislike.setColorFilter(
                        ResourcesCompat.getColor(
                            holder.itemView.context.resources, R.color.dislike, null
                    ))
                }
            }
        }
    }

    private fun dislikeClick(holder: CommentVH, comment: Comment) {
        user?.let {
            if (likesDislikes == null) likesDislikes = ArrayMap<String, Boolean>()
            var likes = comment.like
            var dislikes = comment.dislike
            val userId = user.uid
            val myLikeDislike = likesDislikes?.get(userId)

            myLikeDislike?.let { likeDislike ->
                if (!likeDislike) {
                    comment.dislike = --dislikes
                    holder.tvCountDislikes.text = dislikes.toString()
                    likesDislikes?.remove(userId)
                } else {
                    comment.like = --likes
                    holder.tvCountLikes.text = likes.toString()

                    comment.dislike = ++dislikes
                    holder.tvCountDislikes.text = dislikes.toString()
                    userId.let { likesDislikes?.put(it, false) }
                }
            } ?: run {
                comment.dislike = ++dislikes
                holder.tvCountDislikes.text = dislikes.toString()
                userId.let { likesDislikes?.put(it, false) }
            }
            comment.likesDislikes = this.likesDislikes
            FirebaseHelper.likeOrDislikeComment(comment)
        }
    }

    private fun likeClick(holder: CommentVH, comment: Comment) {
        user?.let {
            if (likesDislikes == null) likesDislikes = ArrayMap<String, Boolean>()
            var likes = comment.like
            var dislikes = comment.dislike
            val userId = user.uid
            val myLikeDislike = likesDislikes?.get(userId)
            myLikeDislike?.let { likeDislike ->
                if (likeDislike) {
                    comment.like = --likes
                    holder.tvCountLikes.text = likes.toString()
                    likesDislikes?.remove(userId)
                } else {
                    comment.dislike = --dislikes
                    holder.tvCountDislikes.text = dislikes.toString()

                    comment.like = ++likes
                    holder.tvCountLikes.text = likes.toString()
                    userId.let { likesDislikes?.put(it, true) }
                }

            } ?: run {
                comment.like = ++likes
                holder.tvCountLikes.text = likes.toString()
                userId.let { likesDislikes?.put(it, true) }
            }
            comment.likesDislikes = this.likesDislikes
            FirebaseHelper.likeOrDislikeComment(comment)
        }
    }

    private fun longItemClick(comment: Comment, holder: CommentVH): Boolean {
        if (clickListener != null) {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                clickListener?.onLongClick(comment, pos)
                return true
            }
        }
        return false
    }

    class CommentVH(itemView: View) : BaseViewHolder(itemView) {
        var tvUserName: TextView = bindView(R.id.tvUserName)
        var tvComment: TextView = bindView(R.id.tvComment)
        var tvDate: TextView = bindView(R.id.tvDate)
        var tvCountLikes: TextView = bindView(R.id.tvCountLikes)
        var tvCountDislikes: TextView = bindView(R.id.tvCountDislike)
        var ivLike: ImageView = bindView(R.id.ivLike)
        var ivDislike: ImageView = bindView(R.id.ivDislike)

    }

    fun setOnNameClickListener(nameClickListener: OnNameClickListener) {
        this.nameClickListener = nameClickListener
    }

    interface OnNameClickListener {
        fun onClick(comment: Comment)
    }
}
