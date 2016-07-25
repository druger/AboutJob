package com.druger.aboutwork.model;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by druger on 24.07.2016.
 */
public class CatalogCompanies implements Comparable<CatalogCompanies> {

    private int id;
    private String name;
    private List<Industries> industries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Industries> getIndustries() {
        return industries;
    }

    public void setIndustries(List<Industries> industries) {
        this.industries = industries;
    }

    @Override
    public int compareTo(@NonNull CatalogCompanies another) {
        return this.name.compareTo(another.name);
    }
}
