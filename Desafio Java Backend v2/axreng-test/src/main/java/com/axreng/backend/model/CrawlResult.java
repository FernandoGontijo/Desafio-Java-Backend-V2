package com.axreng.backend.model;

import com.axreng.backend.enums.StatusEnum;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CrawlResult {

    private StatusEnum status;
    private List<String> urls;
    private Date startSearch;
    private Date endSearch;

    public CrawlResult(StatusEnum status) {
        this.status = status;
        this.startSearch = new Date();
        this.urls = new ArrayList<>();
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Date getStartSearch() {
        return startSearch;
    }

    public void setStartSearch(Date startSearch) {
        this.startSearch = startSearch;
    }

    public Date getEndSearch() {
        return endSearch;
    }

    public void setEndSearch(Date endSearch) {
        this.endSearch = endSearch;
    }


}
