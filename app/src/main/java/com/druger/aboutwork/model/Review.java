package com.druger.aboutwork.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.Utils;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by druger on 10.08.2016.
 */
public class Review extends AbstractItem<Review, Review.ViewHolder> implements Parcelable {

    /**
     * Статусы работника
     */
    public static final int WORKING = 0; // работает
    public static final int WORKED = 1; // работал
    public static final int INTERVIEW = 2; // проходил интервью

    private int companyId;
    private String userId;
    private String userName;
    private long date;
    private String pluses;
    private String minuses;
    private MarkCompany markCompany;
    private int status;
    private String city;
    private String position;
    private long employmentDate;
    private long dismissalDate;
    private long interviewDate;
    private int like;
    private int dislike;
    private boolean myLike;
    private boolean myDislike;

    public Review(int companyId, String userId, String userName, long date) {
        this.companyId = companyId;
        this.userId = userId;
        this.userName = userName;
        this.date = date;
    }

    protected Review(Parcel in) {
        companyId = in.readInt();
        userId = in.readString();
        userName = in.readString();
        date = in.readLong();
        pluses = in.readString();
        minuses = in.readString();
        status = in.readInt();
        city = in.readString();
        position = in.readString();
        employmentDate = in.readLong();
        dismissalDate = in.readLong();
        interviewDate = in.readLong();
        like = in.readInt();
        dislike = in.readInt();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPluses() {
        return pluses;
    }

    public void setPluses(String pluses) {
        this.pluses = pluses;
    }

    public String getMinuses() {
        return minuses;
    }

    public void setMinuses(String minuses) {
        this.minuses = minuses;
    }

    public MarkCompany getMarkCompany() {
        return markCompany;
    }

    public void setMarkCompany(MarkCompany markCompany) {
        this.markCompany = markCompany;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public long getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(long employmentDate) {
        this.employmentDate = employmentDate;
    }

    public long getDismissalDate() {
        return dismissalDate;
    }

    public void setDismissalDate(long dismissalDate) {
        this.dismissalDate = dismissalDate;
    }

    public long getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(long interviewDate) {
        this.interviewDate = interviewDate;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setMyLike(boolean myLike) {
        this.myLike = myLike;
    }

    public void setMyDislike(boolean myDislike) {
        this.myDislike = myDislike;
    }

    @Override
    public int getType() {
        return R.id.item_review;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.review_card;
    }

    @Override
    public void bindView(final ViewHolder holder) {
        super.bindView(holder);
        holder.name.setText(userName);
        holder.date.setText(Utils.getDate(date));
        holder.city.setText(city);
        holder.pluses.setText(pluses);
        holder.minuses.setText(minuses);
        holder.mark.setText(String.valueOf(markCompany.getAverageMark()));
        holder.like.setText(String.valueOf(like));
        holder.dislike.setText(String.valueOf(dislike));

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
                String tagLike = holder.imgLike.getTag().toString();
                String tagDislike = holder.imgDislike.getTag().toString();
                if (tagLike.equalsIgnoreCase("likeInactive")) {
                    holder.imgLike.setTag("likeActive");
                    holder.imgLike.setColorFilter(Color.parseColor("#8BC34A"));
                    like++;
                    myLike = true;
                    holder.like.setText(String.valueOf(like));

                    if (tagDislike.equalsIgnoreCase("dislikeActive")) {
                        holder.imgDislike.setTag("dislikeInactive");
                        holder.imgDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                        dislike--;
                        myDislike = false;
                        holder.dislike.setText(String.valueOf(dislike));
                    }
                } else {
                    holder.imgLike.setTag("likeInactive");
                    holder.imgLike.setColorFilter(Color.parseColor("#9E9E9E"));
                    like--;
                    myLike = false;
                    holder.like.setText(String.valueOf(like));
                }
            }
        });
        holder.imgDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagLike = holder.imgLike.getTag().toString();
                String tagDislike = holder.imgDislike.getTag().toString();
                if (tagDislike.equalsIgnoreCase("dislikeInactive")) {
                    holder.imgDislike.setTag("dislikeActive");
                    holder.imgDislike.setColorFilter(Color.parseColor("#F44336"));
                    dislike++;
                    myDislike = true;
                    holder.dislike.setText(String.valueOf(dislike));

                    if (tagLike.equalsIgnoreCase("likeActive")) {
                        holder.imgLike.setTag("likeInactive");
                        holder.imgLike.setColorFilter(Color.parseColor("#9E9E9E"));
                        like--;
                        myLike = false;
                        holder.like.setText(String.valueOf(like));
                    }
                } else {
                    holder.imgDislike.setTag("dislikeInactive");
                    holder.imgDislike.setColorFilter(Color.parseColor("#9E9E9E"));
                    dislike--;
                    myDislike = false;
                    holder.dislike.setText(String.valueOf(dislike));
                }
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardView;
        protected TextView name;
        protected TextView date;
        protected TextView city;
        protected TextView pluses;
        protected TextView minuses;
        protected TextView mark;
        protected ImageView imgLike;
        protected ImageView imgDislike;
        protected TextView like;
        protected TextView dislike;

        public ViewHolder(View itemView) {
            super(itemView);
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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(companyId);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeLong(date);
        dest.writeString(pluses);
        dest.writeString(minuses);
        dest.writeInt(status);
        dest.writeString(city);
        dest.writeString(position);
        dest.writeLong(employmentDate);
        dest.writeLong(dismissalDate);
        dest.writeLong(interviewDate);
        dest.writeInt(like);
        dest.writeInt(dislike);
    }
}
