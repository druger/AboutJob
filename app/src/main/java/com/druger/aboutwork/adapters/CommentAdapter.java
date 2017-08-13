package com.druger.aboutwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.utils.Utils;

/**
 * Created by druger on 04.03.2017.
 */

public class CommentAdapter extends BaseRecyclerViewAdapter<Comment, CommentAdapter.CommentVH> {

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
        holder.tvDdate.setText(Utils.getDate(comment.getDate()));
        holder.tvCountLikes.setText(String.valueOf(comment.getLike()));

        holder.itemView.setOnLongClickListener(v -> longItemClick(holder, v));
    }

    private boolean longItemClick(CommentVH holder, View v) {
        if (clickListener != null) {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickListener.onLongClick(v, pos);
                return true;
            }
        }
        return false;
    }

    static class CommentVH extends BaseViewHolder {
        ImageView ivAvatar;
        TextView tvUserName;
        TextView tvComment;
        TextView tvDdate;
        TextView tvCountLikes;
        ImageView ivLike;
        ImageView ivReply;

        CommentVH(View itemView) {
            super(itemView);
            ivAvatar =  bindView(R.id.ivAvatar);
            tvUserName =  bindView(R.id.tvUserName);
            tvComment =  bindView(R.id.tvComment);
            tvDdate = bindView(R.id.tvDate);
            tvCountLikes = bindView(R.id.tvCountLikes);
            ivLike = bindView(R.id.ivLike);
            ivReply = bindView(R.id.ivReply);
        }
    }
}
