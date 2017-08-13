package com.druger.aboutwork.adapters;

import android.content.Context;
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
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.model.MarkCompany;
import com.druger.aboutwork.model.Review;
import com.druger.aboutwork.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.druger.aboutwork.Const.Colors.GRAY_500;
import static com.druger.aboutwork.Const.Colors.GREEN_500;
import static com.druger.aboutwork.Const.Colors.RED_500;

/**
 * Created by druger on 29.01.2017.
 */

public class ReviewAdapter extends SelectableAdapter<ReviewAdapter.ReviewVH> {

    private List<Review> reviews;
    private List<Review> deletedReviews;
    private Context context;

    private OnItemClickListener<Review> clickListener;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
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
            holder.ivLike.setTag(context.getString(R.string.like_inactive));
        } else {
            holder.ivLike.setTag(context.getString(R.string.like_active));
            holder.ivLike.setColorFilter(Color.parseColor(GREEN_500));
        }
        if (!myDislike) {
            holder.ivDislike.setTag(context.getString(R.string.dislike_inactive));
        } else {
            holder.ivDislike.setTag(context.getString(R.string.dislike_active));
            holder.ivDislike.setColorFilter(Color.parseColor(RED_500));
        }

        holder.ivLike.setOnClickListener(v -> {
            int like = review.getLike();
            int dislike = review.getDislike();
            String tagLike = holder.ivLike.getTag().toString();
            String tagDislike = holder.ivDislike.getTag().toString();
            if (tagLike.equalsIgnoreCase(context.getString(R.string.like_inactive))) {
                holder.ivLike.setTag(context.getString(R.string.like_active));
                holder.ivLike.setColorFilter(Color.parseColor(GREEN_500));
                review.setLike(++like);
                review.setMyLike(true);
                holder.tvLike.setText(String.valueOf(like));
                FirebaseHelper.setLike(review);

                if (tagDislike.equalsIgnoreCase(context.getString(R.string.dislike_active))) {
                    holder.ivDislike.setTag(context.getString(R.string.dislike_inactive));
                    holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                    review.setDislike(--dislike);
                    review.setMyDislike(false);
                    holder.tvDislike.setText(String.valueOf(dislike));
                    FirebaseHelper.setDislike(review);
                }
            } else {
                holder.ivLike.setTag(context.getString(R.string.like_inactive));
                holder.ivLike.setColorFilter(Color.parseColor(GRAY_500));
                review.setLike(--like);
                review.setMyLike(false);
                holder.tvLike.setText(String.valueOf(like));
                FirebaseHelper.setLike(review);
            }
        });
        holder.ivDislike.setOnClickListener(v -> {
            int like = review.getLike();
            int dislike = review.getDislike();
            String tagLike = holder.ivLike.getTag().toString();
            String tagDislike = holder.ivDislike.getTag().toString();
            if (tagDislike.equalsIgnoreCase(context.getString(R.string.dislike_inactive))) {
                holder.ivDislike.setTag(context.getString(R.string.dislike_active));
                holder.ivDislike.setColorFilter(Color.parseColor(RED_500));
                review.setDislike(++dislike);
                review.setMyDislike(true);
                holder.tvDislike.setText(String.valueOf(dislike));
                FirebaseHelper.setDislike(review);

                if (tagLike.equalsIgnoreCase(context.getString(R.string.like_active))) {
                    holder.ivLike.setTag(context.getString(R.string.like_inactive));
                    holder.ivLike.setColorFilter(Color.parseColor(GRAY_500));
                    review.setLike(--like);
                    review.setMyLike(false);
                    holder.tvLike.setText(String.valueOf(like));
                    FirebaseHelper.setLike(review);
                }
            } else {
                holder.ivDislike.setTag(context.getString(R.string.dislike_inactive));
                holder.ivDislike.setColorFilter(Color.parseColor(GRAY_500));
                review.setDislike(--dislike);
                review.setMyDislike(false);
                holder.tvDislike.setText(String.valueOf(dislike));
                FirebaseHelper.setDislike(review);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            itemClick(holder, review);
        });
        holder.itemView.setOnLongClickListener(v ->
                clickListener != null && clickListener.onLongClick(v, holder.getAdapterPosition()));
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

    static class ReviewVH extends BaseViewHolder {
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

        ReviewVH(View itemView) {
            super(itemView);

            cardView = bindView(R.id.card_view);
            tvName = bindView(R.id.tvUserName);
            tvDate = bindView(R.id.tvDate);
            tvCity = bindView(R.id.tvCity);
            tvPluses = bindView(R.id.tvPluses);
            tvMinuses = bindView(R.id.tvMinuses);
            tvMark = bindView(R.id.tvMark);
            ivLike = bindView(R.id.ivLike);
            ivDislike = bindView(R.id.ivDislike);
            tvLike =  bindView(R.id.tvLike);
            tvDislike =  bindView(R.id.tvDislike);
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
