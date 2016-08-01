package com.druger.aboutwork.model;

import java.util.List;

/**
 * Created by druger on 30.07.2016.
 */
public class CompanyResponse {

    public static final int PER_PAGE = 20;
    private int page;
    private int pages;
    private int found;
    private List<Company> items;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public List<Company> getItems() {
        return items;
    }

    public void setItems(List<Company> items) {
        this.items = items;
    }
}
