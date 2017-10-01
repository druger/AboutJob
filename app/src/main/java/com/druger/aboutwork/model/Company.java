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
        String logo90;
        @SerializedName("240")
        String logo240;
        String original;

        public String getLogo90() {
            return logo90;
        }

        public void setLogo90(String logo90) {
            this.logo90 = logo90;
        }

        public String getLogo240() {
            return logo240;
        }

        public void setLogo240(String logo240) {
            this.logo240 = logo240;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }
}
