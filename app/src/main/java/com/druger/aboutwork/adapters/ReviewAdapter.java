package com.druger.aboutwork.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

            setStatus(reviewVH, review);
            reviewVH.tvPluses.setText(Utils.getQuoteSpan(reviewVH.itemView.getContext(),
                    review.getPluses(), R.color.review_positive));
            reviewVH.tvMinuses.setText(Utils.getQuoteSpan(reviewVH.itemView.getContext(),
                    review.getMinuses(), R.color.review_negative));
            reviewVH.tvName.setText(review.getName());
            reviewVH.tvCity.setText(review.getCity());
            reviewVH.tvDate.setText(Utils.getDate(review.getDate()));
            reviewVH.tvPosition.setText(review.getPosition());

            if (review.getStatus() == Review.INTERVIEW) {
               reviewVH.tvRating.setVisibility(View.GONE);
            } else {
                reviewVH.tvRating.setText(String.valueOf(review.getMarkCompany().getAverageMark()));
            }

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
        TextView tvStatus;
        TextView tvPluses;
        TextView tvMinuses;
        TextView tvName;
        TextView tvCity;
        TextView tvDate;
        TextView tvPosition;
        TextView tvRating;

        ReviewVH(View itemView) {
            super(itemView);

            cvContent = bindView(R.id.cvContent);
            clReviewCard = bindView(R.id.clReviewCard);
            tvStatus = bindView(R.id.tvStatus);
            tvPluses = bindView(R.id.tvPluses);
            tvMinuses = bindView(R.id.tvMinuses);
            tvName = bindView(R.id.tvName);
            tvCity = bindView(R.id.tvCity);
            tvDate = bindView(R.id.tvDate);
            tvPosition = bindView(R.id.tvPosition);
            tvRating = bindView(R.id.tvRating);
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
                positions.subList(0, count).clear();
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            deletedReviews.add(reviews.remove(positionStart));
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public void addReviews(List<Review> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public void addReview(Review review, int position) {
        reviews.add(position, review);
        notifyItemInserted(position);
    }

    public void removeReview(int position) {
        reviews.remove(position);
        notifyItemRemoved(position);
    }
}
