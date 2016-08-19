package com.druger.aboutwork.model;

/**
 * Created by druger on 10.08.2016.
 */
public class Review {

    /**
     * Статусы работника
     */
    public static final int WORKING = 0; // работает
    public static final int WORKED = 1; // работал
    public static final int INTERVIEW = 2; // проходил интервью

    private int companyId;
    private String userId;
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

    public Review(int companyId, String userId, long date) {
        this.companyId = companyId;
        this.userId = userId;
        this.date = date;
    }

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
}
