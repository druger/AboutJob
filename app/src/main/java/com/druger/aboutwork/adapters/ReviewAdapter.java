package com.druger.aboutwork.adapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import com.druger.aboutwork.recyclerview_helper.OnItemClickListener;
import com.druger.aboutwork.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by druger on 29.01.2017.
 */

public class ReviewAdapter extends SelectableAdapter<ReviewAdapter.ReviewVH> {

    private List<Review> reviews;
    private List<Review> deletedReviews;

    private OnItemClickListener clickListener;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        deletedReviews = new ArrayList<>();
    }

    public List<Review> getDeletedReviews() {
        return deletedReviews;
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
        holder.tvName.setText(review.getName());
        holder.tvDate.setText(Utils.getDate(review.getDate()));
        holder.tvCity.setText(review.getCity());
        holder.tvPluses.setText(review.getPluses());
        holder.tvMinuses.setText(review.getMinuses());
        MarkCompany markCompany = review.getMarkCompany();
        if (markCompany != null) {
            holder.tvMark.setText(String.valueOf(markCompany.getAverageMark()));
        }
        holder.tvLike.setText(String.valueOf(review.getLike()));
        holder.tvDislike.setText(String.valueOf(review.getDislike()));
        holder.cardView.setCardBackgroundColor(isSelected(position)
                ? ContextCompat.getColor(holder.cardView.getContext(), R.color.red200) : Color.WHITE);

        boolean myLike = review.isMyLike();
        boolean myDislike = review.isMyDislike();
        if (!myLike) {
            holder.ivLike.setTag("likeInactive");
        } else {
            holder.ivLike.setTag("likeActive");
            holder.ivLike.setColorFilter(Color.parseColor("#8BC34A"));
        }
        if (!myDislike) {
            holder.ivDislike.setTag("dislikeInactive");
        } else {
            holder.ivDislike.setTag("dislikeActive");
            holder.ivDislike.setColorFilter(Color.parseColor("#F44336"));
        }

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like = review.getLike();
                int dislike = review.getDislike();
                String tagLike = holder.ivLike.getTag().toString();
                String tagDislike = holder.ivDislike.getTag().toString();
                if (tagLike.equalsIgnoreCase("likeInactive")) {
                    holder.ivLike.setTag("likeActive");
                    holder.ivLike.setColorFilter(Color.parseColor("#8BC34A"));
                    review.setLike(++like);
                    review.setMyLike(true);
                    holder.tvLike.setText(String.valueOf(like));
                    FirebaseHelper.setLike(review);

                    if (tagDislike.equalsIgnoreCase("dislikeActive")) {
                        holder.ivDislike.setTag("dislikeInactive");
                        holder.ivDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                        review.setDislike(--dislike);
                        review.setMyDislike(false);
                        holder.tvDislike.setText(String.valueOf(dislike));
                        FirebaseHelper.setDislike(review);
                    }
                } else {
                    holder.ivLike.setTag("likeInactive");
                    holder.ivLike.setColorFilter(Color.parseColor("#9E9E9E"));
                    review.setLike(--like);
                    review.setMyLike(false);
                    holder.tvLike.setText(String.valueOf(like));
                    FirebaseHelper.setLike(review);
                }
            }
        });
        holder.ivDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like = review.getLike();
                int dislike = review.getDislike();
                String tagLike = holder.ivLike.getTag().toString();
                String tagDislike = holder.ivDislike.getTag().toString();
                if (tagDislike.equalsIgnoreCase("dislikeInactive")) {
                    holder.ivDislike.setTag("dislikeActive");
                    holder.ivDislike.setColorFilter(Color.parseColor("#F44336"));
                    review.setDislike(++dislike);
                    review.setMyDislike(true);
                    holder.tvDislike.setText(String.valueOf(dislike));
                    FirebaseHelper.setDislike(review);

                    if (tagLike.equalsIgnoreCase("likeActive")) {
                        holder.ivLike.setTag("likeInactive");
                        holder.ivLike.setColorFilter(Color.parseColor("#9E9E9E"));
                        review.setLike(--like);
                        review.setMyLike(false);
                        holder.tvLike.setText(String.valueOf(like));
                        FirebaseHelper.setLike(review);
                    }
                } else {
                    holder.ivDislike.setTag("dislikeInactive");
                    holder.ivDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                    review.setDislike(--dislike);
                    review.setMyDislike(false);
                    holder.tvDislike.setText(String.valueOf(dislike));
                    FirebaseHelper.setDislike(review);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CardView cardView;
        TextView tvName;
        TextView tvDate;
        TextView tvCity;
        TextView tvPluses;
        TextView tvMinuses;
        TextView tvMark;
        ImageView ivLike;
        ImageView ivDislike;
        TextView tvLike;
        TextView tvDislike;

        public ReviewVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tvName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            tvPluses = (TextView) itemView.findViewById(R.id.tvPluses);
            tvMinuses = (TextView) itemView.findViewById(R.id.tvMinuses);
            tvMark = (TextView) itemView.findViewById(R.id.tvMark);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            ivDislike = (ImageView) itemView.findViewById(R.id.ivDislike);
            tvLike = (TextView) itemView.findViewById(R.id.tvLike);
            tvDislike = (TextView) itemView.findViewById(R.id.tvDislike);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return clickListener != null && clickListener.onLongClick(v, getAdapterPosition());
        }
    }

    public void setOnClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private void removeItem(int position) {
        deletedReviews.add(reviews.remove(position));
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        deletedReviews.clear();
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }
                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }
                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            deletedReviews.add(reviews.remove(positionStart));
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}
