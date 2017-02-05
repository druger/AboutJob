package com.druger.aboutwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

/**
 * Created by druger on 24.07.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {

    private String id;
    private String name;
    @JsonIgnore
    @SerializedName("logo_urls")
    private Logo logo;

    public Company() {
    }

    public Company(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

     public class Logo {
        @SerializedName("90")
        String logo_90;
        @SerializedName("240")
        String logo_240;
        String original;

        public String getLogo_90() {
            return logo_90;
        }

        public void setLogo_90(String logo_90) {
            this.logo_90 = logo_90;
        }

        public String getLogo_240() {
            return logo_240;
        }

        public void setLogo_240(String logo_240) {
            this.logo_240 = logo_240;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }
}
