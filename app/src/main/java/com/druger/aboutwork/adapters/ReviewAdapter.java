package com.druger.aboutwork.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.recyclerview_helper.ItemClickListener;
import com.druger.aboutwork.utils.Utils;

import java.util.List;

/**
 * Created by druger on 29.01.2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewVH> {

    private List<Review> reviews;

    private ItemClickListener clickListener;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card, parent, false);
        return new ReviewVH(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewVH holder, int position) {
        final Review review = reviews.get(position);
        holder.name.setText(review.getName());
        holder.date.setText(Utils.getDate(review.getDate()));
        holder.city.setText(review.getCity());
        holder.pluses.setText(review.getPluses());
        holder.minuses.setText(review.getMinuses());
        MarkCompany markCompany = review.getMarkCompany();
        if (markCompany != null) {
            holder.mark.setText(String.valueOf(markCompany.getAverageMark()));
        }
        holder.like.setText(String.valueOf(review.getLike()));
        holder.dislike.setText(String.valueOf(review.getDislike()));

        boolean myLike = review.isMyLike();
        boolean myDislike = review.isMyDislike();
        if (!myLike) {
            holder.imgLike.setTag("likeInactive");
        } else {
            holder.imgLike.setTag("likeActive");
            holder.imgLike.setColorFilter(Color.parseColor("#8BC34A"));
        }
        if (!myDislike) {
            holder.imgDislike.setTag("dislikeInactive");
        } else {
            holder.imgDislike.setTag("dislikeActive");
            holder.imgDislike.setColorFilter(Color.parseColor("#F44336"));
        }

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like = review.getLike();
                int dislike = review.getDislike();
                String tagLike = holder.imgLike.getTag().toString();
                String tagDislike = holder.imgDislike.getTag().toString();
                if (tagLike.equalsIgnoreCase("likeInactive")) {
                    holder.imgLike.setTag("likeActive");
                    holder.imgLike.setColorFilter(Color.parseColor("#8BC34A"));
                    review.setLike(like++);
                    review.setMyLike(true);
                    holder.like.setText(String.valueOf(like));
                    FirebaseHelper.setLike(review);

                    if (tagDislike.equalsIgnoreCase("dislikeActive")) {
                        holder.imgDislike.setTag("dislikeInactive");
                        holder.imgDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                        review.setDislike(dislike--);
                        review.setMyDislike(false);
                        holder.dislike.setText(String.valueOf(dislike));
                        FirebaseHelper.setDislike(review);
                    }
                } else {
                    holder.imgLike.setTag("likeInactive");
                    holder.imgLike.setColorFilter(Color.parseColor("#9E9E9E"));
                    review.setLike(like--);
                    review.setMyLike(false);
                    holder.like.setText(String.valueOf(like));
                    FirebaseHelper.setLike(review);
                }
            }
        });
        holder.imgDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like = review.getLike();
                int dislike = review.getDislike();
                String tagLike = holder.imgLike.getTag().toString();
                String tagDislike = holder.imgDislike.getTag().toString();
                if (tagDislike.equalsIgnoreCase("dislikeInactive")) {
                    holder.imgDislike.setTag("dislikeActive");
                    holder.imgDislike.setColorFilter(Color.parseColor("#F44336"));
                    review.setDislike(dislike++);
                    review.setMyDislike(true);
                    holder.dislike.setText(String.valueOf(dislike));
                    FirebaseHelper.setDislike(review);

                    if (tagLike.equalsIgnoreCase("likeActive")) {
                        holder.imgLike.setTag("likeInactive");
                        holder.imgLike.setColorFilter(Color.parseColor("#9E9E9E"));
                        review.setLike(like--);
                        review.setMyLike(false);
                        holder.like.setText(String.valueOf(like));
                        FirebaseHelper.setLike(review);
                    }
                } else {
                    holder.imgDislike.setTag("dislikeInactive");
                    holder.imgDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                    review.setDislike(dislike--);
                    review.setMyDislike(false);
                    holder.dislike.setText(String.valueOf(dislike));
                    FirebaseHelper.setDislike(review);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView name;
        TextView date;
        TextView city;
        TextView pluses;
        TextView minuses;
        TextView mark;
        ImageView imgLike;
        ImageView imgDislike;
        TextView like;
        TextView dislike;

        public ReviewVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            name = (TextView) itemView.findViewById(R.id.user_name);
            date = (TextView) itemView.findViewById(R.id.date);
            city = (TextView) itemView.findViewById(R.id.city);
            pluses = (TextView) itemView.findViewById(R.id.pluses);
            minuses = (TextView) itemView.findViewById(R.id.minuses);
            mark = (TextView) itemView.findViewById(R.id.mark);
            imgLike = (ImageView) itemView.findViewById(R.id.img_like);
            imgDislike = (ImageView) itemView.findViewById(R.id.img_dislike);
            like = (TextView) itemView.findViewById(R.id.like);
            dislike = (TextView) itemView.findViewById(R.id.dislike);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v, getAdapterPosition());
            }
        }
    }

    public void setOnClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
