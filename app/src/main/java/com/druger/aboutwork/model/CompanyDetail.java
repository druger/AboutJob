package com.druger.aboutwork.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by druger on 04.08.2016.
 */
public class CompanyDetail {

    private String id;
    private String name;
    @SerializedName("site_url")
    private String site;
    private String description;
    @SerializedName("logo_urls")
    private Logo logo;

    public CompanyDetail() {
    }

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

    public static class Logo {
        String original;

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }
}
