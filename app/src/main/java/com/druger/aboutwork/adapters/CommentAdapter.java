package com.druger.aboutwork.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.druger.aboutwork.Const;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.utils.Utils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by druger on 04.03.2017.
 */

public class CommentAdapter extends BaseRecyclerViewAdapter<Comment, CommentAdapter.CommentVH> {

    private OnNameClickListener nameClickListener;

    @Override
    public CommentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflate(R.layout.item_comment, parent);
        return new CommentVH(itemView);
    }

    @Override
    public void onBindViewHolder(CommentVH holder, int position) {
        Comment comment = getItem(position);
        holder.tvUserName.setText(comment.getUserName());
        holder.tvComment.setText(comment.getMessage());
        holder.tvDate.setText(Utils.INSTANCE.getDate(comment.getDate()));
        holder.tvCountLikes.setText(String.valueOf(comment.getLike()));
        holder.tvCountDislikes.setText(String.valueOf(comment.getDislike()));

        holder.itemView.setOnLongClickListener(v -> longItemClick(holder));
        holder.ivLike.setOnClickListener(v -> likeClick(comment));
        holder.ivDislike.setOnClickListener(v -> dislikeClick(comment));
        holder.tvUserName.setOnClickListener(v -> nameClickListener.onClick(comment));

        setColorLike(comment, holder);
        setColorDislike(comment, holder);
    }

    private void setColorDislike(Comment comment, CommentVH holder) {
        if (comment.isMyDislike()) {
            holder.ivDislike.setColorFilter(Color.parseColor(Const.Colors.DISLIKE));
        } else {
            holder.ivDislike.setImageResource(R.drawable.thumb_down);
        }
    }

    private void dislikeClick(Comment comment) {
        int dislike = comment.getDislike();
        if (comment.isMyDislike()) {
            comment.setDislike(--dislike);
            comment.setMyDislike(false);
        } else {
            comment.setDislike(++dislike);
            comment.setMyDislike(true);
        }
        FirebaseHelper.dislikeComment(comment);
    }

    private void setColorLike(Comment comment, CommentVH holder) {
        if (comment.isMyLike()) {
            holder.ivLike.setColorFilter(Color.parseColor(Const.Colors.LIKE));
        } else {
            holder.ivLike.setImageResource(R.drawable.thumb_up);
        }
    }

    private void likeClick(Comment comment) {
        int like = comment.getLike();
        if (comment.isMyLike()) {
            comment.setLike(--like);
            comment.setMyLike(false);
        } else {
            comment.setLike(++like);
            comment.setMyLike(true);
        }
        FirebaseHelper.likeComment(comment);
    }

    private boolean longItemClick(CommentVH holder) {
        if (clickListener != null) {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickListener.onLongClick(pos);
                return true;
            }
        }
        return false;
    }

    static class CommentVH extends BaseViewHolder {
        TextView tvUserName;
        TextView tvComment;
        TextView tvDate;
        TextView tvCountLikes;
        TextView tvCountDislikes;
        ImageView ivLike;
        ImageView ivDislike;
        ImageView ivReply;

        CommentVH(View itemView) {
            super(itemView);
            tvUserName =  bindView(R.id.tvUserName);
            tvComment =  bindView(R.id.tvComment);
            tvDate = bindView(R.id.tvDate);
            tvCountLikes = bindView(R.id.tvCountLikes);
            tvCountDislikes = bindView(R.id.tvCountDislike);
            ivLike = bindView(R.id.ivLike);
            ivDislike = bindView(R.id.ivDislike);
            ivReply = bindView(R.id.ivReply);
        }
    }

    public void setOnNameClickListener(OnNameClickListener nameClickListener) {
        this.nameClickListener = nameClickListener;
    }

    public interface OnNameClickListener {
        void onClick(Comment comment);
    }
}
