package com.druger.aboutwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.utils.Utils;

import java.util.List;

/**
 * Created by druger on 04.03.2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentVH> {

    private List<Comment> comments;
    private OnItemClickListener clickListener;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public CommentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentVH(itemView);
    }

    @Override
    public void onBindViewHolder(CommentVH holder, int position) {
        Comment comment = comments.get(position);
        holder.tvUserName.setText(comment.getUserName());
        holder.tvComment.setText(comment.getMessage());
        holder.tvDdate.setText(Utils.getDate(comment.getDate()));
        holder.tvCountLikes.setText(String.valueOf(comment.getLike()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentVH extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUserName;
        TextView tvComment;
        TextView tvDdate;
        TextView tvCountLikes;
        ImageView ivLike;
        ImageView ivReply;

        public CommentVH(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvDdate = (TextView) itemView.findViewById(R.id.tvDate);
            tvCountLikes = (TextView) itemView.findViewById(R.id.tvCountLikes);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onLongClick(v, position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
