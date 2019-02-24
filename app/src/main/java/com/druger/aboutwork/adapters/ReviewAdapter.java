package com.druger.aboutwork.adapters;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.interfaces.OnItemClickListener;
import com.druger.aboutwork.model.CompanyDetail;
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
    private static final int TYPE_HEADER = 0;
    protected static final int TYPE_ITEM = 1;

    private List<Review> reviews;
    private List<Review> deletedReviews;
    private CompanyDetail companyDetail;

    private OnItemClickListener<Review> clickListener;
    private OnUrlClickListener urlClickListener;
    private OnInfoClickListener infoClickListener;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        deletedReviews = new ArrayList<>();
    }

    public void setCompanyDetail(CompanyDetail companyDetail) {
        this.companyDetail = companyDetail;
    }

    public List<Review> getDeletedReviews() {
        return deletedReviews;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_card, parent, false);
            viewHolder = new ReviewVH(itemView);
        } else if (viewType == TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_reviews, parent, false);
            viewHolder = new HeaderVH(headerView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);
        if (itemType == TYPE_ITEM) {
            ReviewVH reviewVH = (ReviewVH) holder;
            final Review review = reviews.get(position);
            reviewVH.clReviewCard.setBackgroundColor(isSelected(position)
                    ? ContextCompat.getColor(reviewVH.clReviewCard.getContext(), R.color.red200) : Color.WHITE);

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
        } else if (itemType == TYPE_HEADER) {
            HeaderVH headerVH = (HeaderVH) holder;
            headerVH.showCountReviews(getItemCount() - 1);
            headerVH.tvSite.setOnClickListener(v -> urlClickListener.urlClick(companyDetail.getSite()));
            headerVH.tvSite.setText(companyDetail.getSite());
            headerVH.tvCity.setText(companyDetail.getArea().getName());
            headerVH.setSalaryRating(5);
            headerVH.setChiefRating(3);
            headerVH.setWorkplaceRating(2);
            headerVH.setCarrierRating(1);
            headerVH.setCollectiveRating(4);
            headerVH.setSocialPackageRating(5);
            headerVH.setCompanyName(companyDetail.getName());
            headerVH.loadImage(companyDetail);
            headerVH.ivInfo.setOnClickListener(v -> infoClickListener.infoClick(companyDetail.getDescription()));
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

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    static class ReviewVH extends BaseViewHolder {
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

    static class HeaderVH extends BaseViewHolder {
        TextView tvCompanyName;
        TextView tvRating;
        TextView tvCountReviews;
        TextView tvSite;
        TextView tvCity;
        RatingBar ratingCompany;
        ImageView ivRatingSalary;
        ImageView ivRatingChief;
        ImageView ivRatingWorkPlace;
        ImageView ivRatingCareer;
        ImageView ivRatingCollective;
        ImageView ivRatingSocialPackage;
        ImageView ivLogo;
        ImageView ivInfo;

        HeaderVH(View itemView) {
            super(itemView);
            tvCompanyName = bindView(R.id.tvCompanyName);
            tvSite = bindView(R.id.tvSite);
            tvCountReviews = bindView(R.id.tvCountReviews);
            tvRating = bindView(R.id.tvRating);
            ratingCompany = bindView(R.id.ratingBarCompany);
            ivRatingSalary = bindView(R.id.ivRatingSalary);
            ivRatingChief = bindView(R.id.ivRatingChief);
            ivRatingWorkPlace = bindView(R.id.ivRatingWorkPlace);
            ivRatingCareer = bindView(R.id.ivRatingCareer);
            ivRatingCollective = bindView(R.id.ivRatingCollective);
            ivRatingSocialPackage = bindView(R.id.ivRatingSocialPackage);
            ivLogo = bindView(R.id.ivLogo);
            tvCity = bindView(R.id.tvCity);
            ivInfo = bindView(R.id.ivInfo);
        }

        void showCountReviews(int count) {
            tvCountReviews.setText(String.valueOf(count));
        }

        void setSalaryRating(int percent) {
            ivRatingSalary.setImageBitmap(Utils.INSTANCE.crateArcBitmap(itemView.getContext(), percent));
        }

        void setChiefRating(int percent) {
            ivRatingChief.setImageBitmap(Utils.INSTANCE.crateArcBitmap(itemView.getContext(), percent));
        }

        void setWorkplaceRating(int percent) {
            ivRatingWorkPlace.setImageBitmap(Utils.INSTANCE.crateArcBitmap(itemView.getContext(), percent));
        }

        void setCarrierRating(int percent) {
            ivRatingCareer.setImageBitmap(Utils.INSTANCE.crateArcBitmap(itemView.getContext(), percent));
        }

        void setCollectiveRating(int percent) {
            ivRatingCollective.setImageBitmap(Utils.INSTANCE.crateArcBitmap(itemView.getContext(), percent));
        }

        void setSocialPackageRating(int percent) {
            ivRatingSocialPackage.setImageBitmap(Utils.INSTANCE.crateArcBitmap(itemView.getContext(), percent));
        }

        void setCompanyName(String name) {
            tvCompanyName.setText(name);
        }

        void loadImage(CompanyDetail company) {
            CompanyDetail.Logo logo = company.getLogo();
            Glide.with(itemView.getContext())
                    .load(logo != null ? logo.getOriginal() : "")
                    .placeholder(R.drawable.ic_default_company)
                    .error(R.drawable.ic_default_company)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivLogo);
        }
    }

    public void setUrlClickListener(OnUrlClickListener urlClickListener) {
        this.urlClickListener = urlClickListener;
    }

    public void setInfoClickListener(OnInfoClickListener infoClickListener) {
        this.infoClickListener = infoClickListener;
    }

    public interface OnUrlClickListener {
        void urlClick(String site);
    }

    public interface OnInfoClickListener {
        void infoClick(String description);
    }
}
