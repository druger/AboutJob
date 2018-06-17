package com.druger.aboutwork.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by druger on 04.08.2016.
 */
public class CompanyDetail implements Parcelable {

    private String id;
    private String name;
    @SerializedName("site_url")
    private String site;
    private String description;
    @SerializedName("logo_urls")
    private Logo logo;
    private Area area;

    public CompanyDetail() {
    }

    protected CompanyDetail(Parcel in) {
        id = in.readString();
        name = in.readString();
        site = in.readString();
        description = in.readString();
        logo = in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<CompanyDetail> CREATOR = new Creator<CompanyDetail>() {
        @Override
        public CompanyDetail createFromParcel(Parcel in) {
            return new CompanyDetail(in);
        }

        @Override
        public CompanyDetail[] newArray(int size) {
            return new CompanyDetail[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public Area getArea() {
        return area;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeString(description);
        dest.writeParcelable(logo, PARCELABLE_WRITE_RETURN_VALUE);
    }

    // TODO вынести в отдельный класс, т.к аналогичный класс исп. в классе Company
    public static class Logo implements Parcelable {
        String original;
        @SerializedName("90")
        String logo90;

        protected Logo(Parcel in) {
            original = in.readString();
        }

        public static final Creator<Logo> CREATOR = new Creator<Logo>() {
            @Override
            public Logo createFromParcel(Parcel in) {
                return new Logo(in);
            }

            @Override
            public Logo[] newArray(int size) {
                return new Logo[size];
            }
        };

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        public String getLogo90() {
            return logo90;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(original);
        }
    }

    public static class Area {
        String name;

        public String getName() {
            return name;
        }
    }
}
