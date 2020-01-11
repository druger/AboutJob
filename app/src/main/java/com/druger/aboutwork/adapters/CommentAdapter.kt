package com.druger.aboutwork.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.druger.aboutwork.Const.Colors.DISLIKE
import com.druger.aboutwork.Const.Colors.GRAY_500
import com.druger.aboutwork.Const.Colors.LIKE
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

        holder.itemView.setOnLongClickListener { longItemClick(holder) }
        holder.ivLike.setOnClickListener { likeClick(holder, comment) }
        holder.ivDislike.setOnClickListener { dislikeClick(holder, comment) }
        holder.tvUserName.setOnClickListener { nameClickListener?.onClick(comment) }

        setColorLike(comment, holder)
        setColorDislike(comment, holder)
    }

    private fun setColorDislike(comment: Comment, holder: CommentVH) {
        if (comment.myDislike) {
            holder.ivDislike.setColorFilter(Color.parseColor(DISLIKE))
        } else {
            holder.ivDislike.setImageResource(R.drawable.thumb_down)
        }
    }

    private fun dislikeClick(holder: CommentVH, comment: Comment) {
        if (user != null) {
            var dislike = comment.dislike
            var like = comment.like
            if (comment.myDislike) {
                comment.dislike = --dislike
                comment.myDislike = false
                holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500))
            } else {
                comment.dislike = ++dislike
                comment.myDislike = true
                holder.ivDislike.setColorFilter(Color.parseColor(DISLIKE))

                if (comment.myLike) {
                    holder.ivLike.setColorFilter(Color.parseColor(GRAY_500))
                    comment.like = --like
                    comment.myLike = false
                    FirebaseHelper.likeComment(comment)
                }
            }
            FirebaseHelper.dislikeComment(comment)
        }
    }

    private fun setColorLike(comment: Comment, holder: CommentVH) {
        if (comment.myLike) {
            holder.ivLike.setColorFilter(Color.parseColor(LIKE))
        } else {
            holder.ivLike.setImageResource(R.drawable.thumb_up)
        }
    }

    private fun likeClick(holder: CommentVH, comment: Comment) {
        if (user != null) {
            var like = comment.like
            var dislike = comment.dislike
            if (comment.myLike) {
                comment.like = --like
                comment.myLike = false
                holder.ivLike.setColorFilter(Color.parseColor(GRAY_500))
            } else {
                comment.like = ++like
                comment.myLike = true
                holder.ivLike.setColorFilter(Color.parseColor(LIKE))

                if (comment.myDislike) {
                    holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500))
                    comment.dislike = --dislike
                    comment.myDislike = false
                    FirebaseHelper.dislikeComment(comment)
                }
            }
            FirebaseHelper.likeComment(comment)
        }
    }

    private fun longItemClick(holder: CommentVH): Boolean {
        if (clickListener != null) {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                clickListener?.onLongClick(pos)
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
