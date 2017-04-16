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
        holder.cardView.setCardBackgroundColor(isSelected(position)
                ? ContextCompat.getColor(holder.cardView.getContext(), R.color.red200) : Color.WHITE);

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
                    review.setLike(++like);
                    review.setMyLike(true);
                    holder.like.setText(String.valueOf(like));
                    FirebaseHelper.setLike(review);

                    if (tagDislike.equalsIgnoreCase("dislikeActive")) {
                        holder.imgDislike.setTag("dislikeInactive");
                        holder.imgDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                        review.setDislike(--dislike);
                        review.setMyDislike(false);
                        holder.dislike.setText(String.valueOf(dislike));
                        FirebaseHelper.setDislike(review);
                    }
                } else {
                    holder.imgLike.setTag("likeInactive");
                    holder.imgLike.setColorFilter(Color.parseColor("#9E9E9E"));
                    review.setLike(--like);
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
                    review.setDislike(++dislike);
                    review.setMyDislike(true);
                    holder.dislike.setText(String.valueOf(dislike));
                    FirebaseHelper.setDislike(review);

                    if (tagLike.equalsIgnoreCase("likeActive")) {
                        holder.imgLike.setTag("likeInactive");
                        holder.imgLike.setColorFilter(Color.parseColor("#9E9E9E"));
                        review.setLike(--like);
                        review.setMyLike(false);
                        holder.like.setText(String.valueOf(like));
                        FirebaseHelper.setLike(review);
                    }
                } else {
                    holder.imgDislike.setTag("dislikeInactive");
                    holder.imgDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                    review.setDislike(--dislike);
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

    public class ReviewVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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
            itemView.setOnLongClickListener(this);

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
