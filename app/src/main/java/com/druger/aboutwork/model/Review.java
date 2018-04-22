package com.druger.aboutwork.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by druger on 10.08.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Review extends BaseObservable implements Parcelable {

    /**
     * Статусы работника
     */
    public static final int WORKING = 0;
    public static final int WORKED = 1;
    public static final int INTERVIEW = 2;

    private String companyId;
    private String userId;
    @JsonIgnore
    private String name;
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
    @JsonIgnore
    private String firebaseKey;

    public Review() {
    }

    public Review(String companyId, String userId, long date) {
        this.companyId = companyId;
        this.userId = userId;
        this.date = date;
    }

    protected Review(Parcel in) {
        companyId = in.readString();
        userId = in.readString();
        name = in.readString();
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

    @Bindable
    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
        notifyPropertyChanged(BR.like);
    }

    @Bindable
    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
        notifyPropertyChanged(BR.dislike);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMyLike(boolean myLike) {
        this.myLike = myLike;
    }

    public void setMyDislike(boolean myDislike) {
        this.myDislike = myDislike;
    }

    public boolean isMyLike() {
        return myLike;
    }

    public boolean isMyDislike() {
        return myDislike;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(companyId);
        dest.writeString(userId);
        dest.writeString(name);
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
