package com.druger.aboutwork.adapters;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
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
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.druger.aboutwork.Const.Colors.DISLIKE;
import static com.druger.aboutwork.Const.Colors.GRAY_500;
import static com.druger.aboutwork.Const.Colors.LIKE;

/**
 * Created by druger on 29.01.2017.
 */

public class ReviewAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    protected List<Review> reviews;
    private List<Review> deletedReviews;

    private OnItemClickListener<Review> clickListener;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        deletedReviews = new ArrayList<>();
    }

    public List<Review> getDeletedReviews() {
        return deletedReviews;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card, parent, false);
        return new ReviewVH(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReviewVH reviewVH = (ReviewVH) holder;
        if (!reviews.isEmpty()) {
            final Review review = reviews.get(position);
            reviewVH.clReviewCard.setBackgroundColor(isSelected(position)
                    ? ContextCompat.getColor(reviewVH.clReviewCard.getContext(), R.color.selected_review) : Color.WHITE);

            setColorLikeAndDislike(reviewVH, review);
            onLikeClick(reviewVH, review);
            onDislikeClick(reviewVH, review);
            setStatus(reviewVH, review);
            reviewVH.tvPluses.setText(Utils.getQuoteSpan(reviewVH.itemView.getContext(),
                    review.getPluses(), R.color.review_positive));
            reviewVH.tvMinuses.setText(Utils.getQuoteSpan(reviewVH.itemView.getContext(),
                    review.getMinuses(), R.color.review_negative));
            reviewVH.tvName.setText(review.getName());
            reviewVH.tvCity.setText(review.getCity());
            reviewVH.tvDate.setText(Utils.getDate(review.getDate()));
            reviewVH.tvPosition.setText(review.getPosition());
            reviewVH.tvRating.setText(String.valueOf(review.getMarkCompany().getAverageMark()));
            reviewVH.tvDislike.setText(String.valueOf(review.getDislike()));
            reviewVH.tvLike.setText(String.valueOf(review.getLike()));

            holder.itemView.setOnClickListener(v -> itemClick(reviewVH, review));
            holder.itemView.setOnLongClickListener(v ->
                    clickListener != null && clickListener.onLongClick(reviewVH.getAdapterPosition()));
        }
    }

    private void setStatus(ReviewVH reviewVH, Review review) {
        switch (review.getStatus()) {
            case 0:
                reviewVH.tvStatus.setText(R.string.working);
                break;
            case 1:
                reviewVH.tvStatus.setText(R.string.worked);
                break;
            case 2:
                reviewVH.tvStatus.setText(R.string.interview);
                break;
        }
    }

    private void setColorLikeAndDislike(ReviewVH reviewVH, Review review) {
        boolean myLike = review.isMyLike();
        boolean myDislike = review.isMyDislike();
        if (!myLike) {
            reviewVH.ivLike.setColorFilter(Color.parseColor(GRAY_500));
        } else {
            reviewVH.ivLike.setColorFilter(Color.parseColor(LIKE));
        }
        if (!myDislike) {
            reviewVH.ivDislike.setColorFilter(Color.parseColor(GRAY_500));
        } else {
            reviewVH.ivDislike.setColorFilter(Color.parseColor(DISLIKE));
        }
    }

    private void onDislikeClick(ReviewVH holder, Review review) {
        holder.ivDislike.setOnClickListener(v -> {
            int like = review.getLike();
            int dislike = review.getDislike();
            if (!review.isMyDislike()) {
                holder.ivDislike.setColorFilter(Color.parseColor(DISLIKE));
                review.setDislike(++dislike);
                review.setMyDislike(true);

                if (review.isMyLike()) {
                    holder.ivLike.setColorFilter(Color.parseColor(GRAY_500));
                    review.setLike(--like);
                    review.setMyLike(false);
                    FirebaseHelper.likeReview(review);
                }
            } else {
                holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                review.setDislike(--dislike);
                review.setMyDislike(false);
            }
            FirebaseHelper.dislikeReview(review);
        });
    }

    private void onLikeClick(ReviewVH holder, Review review) {
        holder.ivLike.setOnClickListener(v -> {
            int like = review.getLike();
            int dislike = review.getDislike();
            if (!review.isMyLike()) {
                holder.ivLike.setColorFilter(Color.parseColor(LIKE));
                review.setLike(++like);
                review.setMyLike(true);

                if (review.isMyDislike()) {
                    holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                    review.setDislike(--dislike);
                    review.setMyDislike(false);
                    FirebaseHelper.dislikeReview(review);
                }
            } else {
                holder.ivLike.setColorFilter(Color.parseColor(GRAY_500));
                review.setLike(--like);
                review.setMyLike(false);
            }
            FirebaseHelper.likeReview(review);
        });
    }

    private void itemClick(ReviewVH holder, Review review) {
        if (clickListener != null) {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickListener.onClick(review, pos);
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewVH extends BaseViewHolder {
        public CardView cvContent;
        ConstraintLayout clReviewCard;
        ImageView ivLike;
        ImageView ivDislike;
        TextView tvStatus;
        TextView tvPluses;
        TextView tvMinuses;
        TextView tvName;
        TextView tvCity;
        TextView tvDate;
        TextView tvPosition;
        TextView tvRating;
        TextView tvDislike;
        TextView tvLike;

        ReviewVH(View itemView) {
            super(itemView);

            cvContent = bindView(R.id.cvContent);
            clReviewCard = bindView(R.id.clReviewCard);
            ivLike = bindView(R.id.ivLike);
            ivDislike = bindView(R.id.ivDislike);
            tvStatus = bindView(R.id.tvStatus);
            tvPluses = bindView(R.id.tvPluses);
            tvMinuses = bindView(R.id.tvMinuses);
            tvName = bindView(R.id.tvName);
            tvCity = bindView(R.id.tvCity);
            tvDate = bindView(R.id.tvDate);
            tvPosition = bindView(R.id.tvPosition);
            tvRating = bindView(R.id.tvRating);
            tvDislike = bindView(R.id.tvDislike);
            tvLike = bindView(R.id.tvLike);
        }
    }

    public void setOnClickListener(OnItemClickListener<Review> clickListener) {
        this.clickListener = clickListener;
    }

    private void removeItem(int position) {
        deletedReviews.add(reviews.remove(position));
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        deletedReviews.clear();
        Collections.sort(positions, (o1, o2) -> o2 - o1);

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
