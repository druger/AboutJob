package com.druger.aboutwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.recyclerview_helper.OnItemClickListener;
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
        holder.userName.setText(comment.getUserName());
        holder.comment.setText(comment.getMessage());
        holder.date.setText(Utils.getDate(comment.getDate()));
        holder.countLikes.setText(String.valueOf(comment.getLike()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentVH extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView userName;
        TextView comment;
        TextView date;
        TextView countLikes;
        ImageView like;
        ImageView reply;

        public CommentVH(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            comment = (TextView) itemView.findViewById(R.id.comment);
            date = (TextView) itemView.findViewById(R.id.date);
            countLikes = (TextView) itemView.findViewById(R.id.count_likes);
            like = (ImageView) itemView.findViewById(R.id.like);
            reply = (ImageView) itemView.findViewById(R.id.reply);

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
