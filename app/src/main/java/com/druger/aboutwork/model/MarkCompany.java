package com.druger.aboutwork.model;

import java.math.BigDecimal;

/**
 * Created by druger on 10.08.2016.
 */
public class MarkCompany {

    private String userId;
    private int companyId;
    private float salary;
    private float chief;
    private float workplace;
    private float career;
    private float collective;
    private float socialPackage;

    public MarkCompany(String userId, int companyId) {
        this.userId = userId;
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public float getSalary() {
        return salary;
    }

    public float getChief() {
        return chief;
    }

    public float getWorkplace() {
        return workplace;
    }

    public float getCareer() {
        return career;
    }

    public float getCollective() {
        return collective;
    }

    public float getSocialPackage() {
        return socialPackage;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setChief(float chief) {
        this.chief = chief;
    }

    public void setWorkplace(float workplace) {
        this.workplace = workplace;
    }

    public void setCareer(float career) {
        this.career = career;
    }

    public void setCollective(float collective) {
        this.collective = collective;
    }

    public void setSocialPackage(float socialPackage) {
        this.socialPackage = socialPackage;
    }

    public float getRating() {
        if (salary != 0 && chief != 0 && workplace != 0
                && career != 0 && collective != 0 && socialPackage != 0) {
            float rating = (salary + chief + workplace + career + collective + socialPackage) / 6;
            return roundRating(rating, 2);
        }
        return 0;
    }

    private float roundRating(float rating, int scale) {
        return BigDecimal.valueOf(rating).setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
