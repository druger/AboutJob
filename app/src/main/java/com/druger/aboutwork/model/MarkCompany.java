package com.druger.aboutwork.model;

import java.math.BigDecimal;

/**
 * Created by druger on 10.08.2016.
 */
public class MarkCompany {

    private String userId;
    private String companyId;
    private float salary;
    private float chief;
    private float workplace;
    private float career;
    private float collective;
    private float socialPackage;

    public MarkCompany() {
    }

    public MarkCompany(String userId, String companyId) {
        this.userId = userId;
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCompanyId() {
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public float getAverageMark() {
        if (salary != 0 && chief != 0 && workplace != 0
                && career != 0 && collective != 0 && socialPackage != 0) {
            float rating = (salary + chief + workplace + career + collective + socialPackage) / 6;
            return roundMark(rating);
        }
        return 0;
    }

    public static float roundMark(float rating) {
        return BigDecimal.valueOf(rating).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
