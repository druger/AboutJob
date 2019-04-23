package com.druger.aboutwork.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.Const;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.utils.Utils;

import static com.druger.aboutwork.Const.Colors.DISLIKE;
import static com.druger.aboutwork.Const.Colors.GRAY_500;
import static com.druger.aboutwork.Const.Colors.LIKE;

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
        holder.ivLike.setOnClickListener(v -> likeClick(holder, comment));
        holder.ivDislike.setOnClickListener(v -> dislikeClick(holder, comment));
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

    private void dislikeClick(CommentVH holder, Comment comment) {
        int dislike = comment.getDislike();
        int like = comment.getLike();
        if (comment.isMyDislike()) {
            comment.setDislike(--dislike);
            comment.setMyDislike(false);
            holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500));
        } else {
            comment.setDislike(++dislike);
            comment.setMyDislike(true);
            holder.ivDislike.setColorFilter(Color.parseColor(DISLIKE));

            if (comment.isMyLike()) {
                holder.ivLike.setColorFilter(Color.parseColor(GRAY_500));
                comment.setLike(--like);
                comment.setMyLike(false);
                FirebaseHelper.INSTANCE.likeComment(comment);
            }
        }
        FirebaseHelper.INSTANCE.dislikeComment(comment);
    }

    private void setColorLike(Comment comment, CommentVH holder) {
        if (comment.isMyLike()) {
            holder.ivLike.setColorFilter(Color.parseColor(Const.Colors.LIKE));
        } else {
            holder.ivLike.setImageResource(R.drawable.thumb_up);
        }
    }

    private void likeClick(CommentVH holder, Comment comment) {
        int like = comment.getLike();
        int dislike = comment.getDislike();
        if (comment.isMyLike()) {
            comment.setLike(--like);
            comment.setMyLike(false);
            holder.ivLike.setColorFilter(Color.parseColor(GRAY_500));
        } else {
            comment.setLike(++like);
            comment.setMyLike(true);
            holder.ivLike.setColorFilter(Color.parseColor(LIKE));

            if (comment.isMyDislike()) {
                holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                comment.setDislike(--dislike);
                comment.setMyDislike(false);
                FirebaseHelper.INSTANCE.dislikeComment(comment);
            }
        }
        FirebaseHelper.INSTANCE.likeComment(comment);
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
