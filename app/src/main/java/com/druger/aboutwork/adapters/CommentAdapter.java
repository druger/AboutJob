package com.druger.aboutwork.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.Comment;
import com.druger.aboutwork.utils.Utils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by druger on 04.03.2017.
 */

public class CommentAdapter extends BaseRecyclerViewAdapter<Comment, CommentAdapter.CommentVH> {

    private Context context;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;

    public CommentAdapter(Context context) {
        super();
        this.context = context;
        firebaseStorage = FirebaseStorage.getInstance();
    }

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

        holder.itemView.setOnLongClickListener(v -> longItemClick(holder));
        holder.ivLike.setOnClickListener(v -> likeClick(comment));

        setColorLike(comment, holder);
        downLoadPhoto(comment.getUserId(), holder);
    }

    private void downLoadPhoto(String userId, CommentVH holder) {
        storageRef = FirebaseHelper.downloadPhoto(firebaseStorage, userId);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .crossFade()
                .error(R.drawable.ic_account_circle_black)
                .into(holder.ivAvatar);
    }

    private void setColorLike(Comment comment, CommentVH holder) {
        if (comment.isMyLike()) {
            holder.ivLike.setImageResource(R.drawable.ic_heart);
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_heart_outline);
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
        CircleImageView ivAvatar;
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
